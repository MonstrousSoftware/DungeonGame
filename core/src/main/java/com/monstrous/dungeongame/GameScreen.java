package com.monstrous.dungeongame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;
import net.mgsx.gltf.scene3d.attributes.PBRCubemapAttribute;
import net.mgsx.gltf.scene3d.attributes.PBRTextureAttribute;
import net.mgsx.gltf.scene3d.lights.DirectionalLightEx;
import net.mgsx.gltf.scene3d.scene.SceneManager;

import net.mgsx.gltf.scene3d.utils.IBLBuilder;


public class GameScreen extends ScreenAdapter {
    public final static int MAP_WIDTH = 50;
    public final static int MAP_HEIGHT = 50;


    private GUI gui;
    private SceneManager sceneManager;
    private OrthographicCamera camera;
    private Cubemap diffuseCubemap;
    private Cubemap environmentCubemap;
    private Cubemap specularCubemap;
    private Texture brdfLUT;
    private DirectionalLightEx light;
    private DungeonScenes dungeonScenes;
    private DungeonMap map;
    private GameObjectTypes gameObjectTypes;
    //private GameObjects gameObjects;
    private OrthoCamController camController;
    private KeyController keyController;


    @Override
    public void show() {

        sceneManager = new SceneManager(45);

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
        light = new DirectionalLightEx();
        light.direction.set(1, -3, 1).nor();
        light.color.set(Color.WHITE);
        sceneManager.environment.add(light);

        // setup quick IBL (image based lighting)
        IBLBuilder iblBuilder = IBLBuilder.createOutdoor(light);
        environmentCubemap = iblBuilder.buildEnvMap(1024);
        diffuseCubemap = iblBuilder.buildIrradianceMap(256);
        specularCubemap = iblBuilder.buildRadianceMap(10);
        iblBuilder.dispose();

        // This texture is provided by the library, no need to have it in your assets.
        brdfLUT = new Texture(Gdx.files.classpath("net/mgsx/gltf/shaders/brdfLUT.png"));

        sceneManager.setAmbientLight(1f);
        sceneManager.environment.set(new PBRTextureAttribute(PBRTextureAttribute.BRDFLUTTexture, brdfLUT));
        sceneManager.environment.set(PBRCubemapAttribute.createSpecularEnv(specularCubemap));
        sceneManager.environment.set(PBRCubemapAttribute.createDiffuseEnv(diffuseCubemap));

        gameObjectTypes = new GameObjectTypes();
        map = new DungeonMap(1234, 0, MAP_WIDTH, MAP_HEIGHT);

        //gameObjects = new GameObjects(MAP_WIDTH, MAP_HEIGHT);

        dungeonScenes = new DungeonScenes(sceneManager);
        dungeonScenes.buildMap( map);
        dungeonScenes.populateMap( map );

        dungeonScenes.placeRogue( map );


        camController = new OrthoCamController(camera, dungeonScenes.getRogue().scene.modelInstance);
        keyController = new KeyController(map, dungeonScenes );

        gui = new GUI( dungeonScenes.getRogue() );

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

        camController.update(deltaTime);

        // render
        ScreenUtils.clear(Color.LIGHT_GRAY, true);
        sceneManager.update(deltaTime);
        sceneManager.render();

        gui.render(deltaTime);
    }

    @Override
    public void resize(int width, int height) {

        sceneManager.updateViewport(width, height);
        gui.resize(width, height);
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
    }
}
