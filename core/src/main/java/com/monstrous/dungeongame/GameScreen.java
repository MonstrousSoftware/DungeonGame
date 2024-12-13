package com.monstrous.dungeongame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g3d.shaders.DepthShader;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;
import net.mgsx.gltf.scene3d.attributes.PBRCubemapAttribute;
import net.mgsx.gltf.scene3d.attributes.PBRFloatAttribute;
import net.mgsx.gltf.scene3d.attributes.PBRTextureAttribute;
import net.mgsx.gltf.scene3d.lights.DirectionalLightEx;
import net.mgsx.gltf.scene3d.lights.DirectionalShadowLight;
import net.mgsx.gltf.scene3d.lights.PointLightEx;
import net.mgsx.gltf.scene3d.scene.SceneManager;

import net.mgsx.gltf.scene3d.shaders.PBRDepthShaderProvider;
import net.mgsx.gltf.scene3d.shaders.PBRShaderConfig;
import net.mgsx.gltf.scene3d.shaders.PBRShaderProvider;
import net.mgsx.gltf.scene3d.utils.IBLBuilder;


public class GameScreen extends ScreenAdapter {
    final static int SHADOW_MAP_SIZE = 2048;

    private Main game;
    private World world;
    private GUI gui;
    private SceneManager sceneManager;
    private OrthographicCamera camera;
    private Cubemap diffuseCubemap;
    private Cubemap environmentCubemap;
    private Cubemap specularCubemap;
    private Texture brdfLUT;
    private DirectionalLightEx light;
    private DungeonScenes dungeonScenes;
    private OrthoCamController camController;
    private KeyController keyController;
    private PointLightEx pointLight;
    private DirectionalShadowLight shadowCastingLight;
    private FrameBuffer fbo;
    private Filter filter;


    public GameScreen(Main game) {
        this.game = game;
        this.world = game.world;
    }

    @Override
    public void show() {

        // create a scene manager allowing for required number of bones and only the number of lights we need
        PBRShaderConfig config = PBRShaderProvider.createDefaultConfig();
        config.numBones = 45;
        config.numDirectionalLights = 1;
        config.numPointLights = 6;

        DepthShader.Config depthConfig = PBRShaderProvider.createDefaultDepthConfig();
        depthConfig.numBones = 45;

        sceneManager = new SceneManager(new PBRShaderProvider(config), new PBRDepthShaderProvider(depthConfig));


        // setup camera
        camera = new OrthographicCamera();
        camera.near = -500f;
        camera.far = 500;
        camera.position.set(5,5, 5);
        camera.zoom = 0.03f;
        camera.up.set(Vector3.Y);
        camera.lookAt( new Vector3(0, 0, 0));


        camera.update();
        sceneManager.setCamera(camera);



        // setup light

        pointLight = new PointLightEx();
        pointLight.color.set(Color.YELLOW);
        pointLight.range = 8f;
        pointLight.intensity = 3f;
        sceneManager.environment.add(pointLight);

        // setup shadow light
        shadowCastingLight = new DirectionalShadowLight(SHADOW_MAP_SIZE, SHADOW_MAP_SIZE);
        shadowCastingLight.direction.set(-3, -2, -3).nor();
        shadowCastingLight.color.set(Color.YELLOW);
        shadowCastingLight.intensity = .5f;
        shadowCastingLight.setViewport(64, 64, 0.1f, 50f);

        sceneManager.environment.add(shadowCastingLight);
        sceneManager.environment.set(new PBRFloatAttribute(PBRFloatAttribute.ShadowBias, 1f/256f));

        // setup quick IBL (image based lighting)
        IBLBuilder iblBuilder = IBLBuilder.createOutdoor(shadowCastingLight);
        environmentCubemap = iblBuilder.buildEnvMap(1024);
        diffuseCubemap = iblBuilder.buildIrradianceMap(256);
        specularCubemap = iblBuilder.buildRadianceMap(10);
        iblBuilder.dispose();

        // This texture is provided by the library, no need to have it in your assets.
        brdfLUT = new Texture(Gdx.files.classpath("net/mgsx/gltf/shaders/brdfLUT.png"));

        sceneManager.setAmbientLight(0.0f);
        sceneManager.environment.set(new PBRTextureAttribute(PBRTextureAttribute.BRDFLUTTexture, brdfLUT));
        sceneManager.environment.set(PBRCubemapAttribute.createSpecularEnv(specularCubemap));
        sceneManager.environment.set(PBRCubemapAttribute.createDiffuseEnv(diffuseCubemap));

        dungeonScenes = new DungeonScenes(sceneManager);
        world.isRebuilt = false;
        dungeonScenes.createRogueModel( world );
        dungeonScenes.liftFog( world );
        dungeonScenes.buildMap( world.map );
        dungeonScenes.buildCorridors( world.map );
        dungeonScenes.populateMap(world);


        camController = new OrthoCamController(camera, null);
        keyController = new KeyController(world, dungeonScenes );

        gui = new GUI( world );
        filter = new Filter();

        InputMultiplexer im = new InputMultiplexer();
        im.addProcessor(camController);
        im.addProcessor(keyController);
        im.addProcessor(gui.stage);
        Gdx.input.setInputProcessor(im);

        MessageBox mb = new MessageBox();
        MessageBox.addLine("Welcome traveller!");
    }



    @Override
    public void render(float deltaTime) {
        if(Gdx.input.isKeyJustPressed(Input.Keys.M)){
            game.setScreen( new MapScreen(game) );
            return;
        }


        if(world.isRebuilt){
            world.isRebuilt = false;

            // refill scene manager
            sceneManager.getRenderableProviders().clear();
            dungeonScenes.createRogueModel( world );
            dungeonScenes.liftFog( world );
            int roomId = world.map.roomCode[world.rogue.y][world.rogue.x];
            Room room = world.map.rooms.get(roomId);
            dungeonScenes.buildRoom( world.map, room );
            dungeonScenes.populateRoom(world, room);
            camController.setTrackedObject( world.rogue.scene.modelInstance );
        }

        //camController.setTrackedObject( world.rogue.scene.modelInstance );
        camController.update(deltaTime);

        world.rogue.scene.modelInstance.transform.getTranslation(pointLight.position);
        pointLight.position.y = 3;
        shadowCastingLight.setCenter(pointLight.position);

        // render
        sceneManager.renderShadows();
        fbo.begin();
        ScreenUtils.clear(Color.BLACK, true);
        sceneManager.update(deltaTime);
        sceneManager.renderColors();
        fbo.end();

        ScreenUtils.clear(new Color(6/255f,0,30/255f,1), false);
        filter.render(fbo,0,0, Gdx.graphics.getWidth() - GUI.PANEL_WIDTH, Gdx.graphics.getHeight());

        gui.render(deltaTime);
    }

    @Override
    public void resize(int width, int height) {
        if(fbo != null)
            fbo.dispose();
        fbo = new FrameBuffer(Pixmap.Format.RGBA8888, width - GUI.PANEL_WIDTH, height, true);

        sceneManager.updateViewport(width - GUI.PANEL_WIDTH, height);
        gui.resize(width, height);
        filter.resize(width, height);
    }

    @Override
    public void hide() {
        // This method is called when another screen replaces this one.
    }

    @Override
    public void dispose() {
        sceneManager.dispose();
        dungeonScenes.dispose();
        environmentCubemap.dispose();
        diffuseCubemap.dispose();
        specularCubemap.dispose();
        brdfLUT.dispose();
        gui.dispose();
        filter.dispose();
    }
}
