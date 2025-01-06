package com.monstrous.dungeongame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g3d.shaders.DepthShader;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.ScreenUtils;
import net.mgsx.gltf.scene3d.attributes.PBRCubemapAttribute;
import net.mgsx.gltf.scene3d.attributes.PBRFloatAttribute;
import net.mgsx.gltf.scene3d.attributes.PBRTextureAttribute;
import net.mgsx.gltf.scene3d.lights.DirectionalLightEx;
import net.mgsx.gltf.scene3d.lights.DirectionalShadowLight;
import net.mgsx.gltf.scene3d.lights.PointLightEx;
import net.mgsx.gltf.scene3d.scene.Scene;
import net.mgsx.gltf.scene3d.scene.SceneAsset;
import net.mgsx.gltf.scene3d.scene.SceneManager;
import net.mgsx.gltf.scene3d.shaders.PBRDepthShaderProvider;
import net.mgsx.gltf.scene3d.shaders.PBRShaderConfig;
import net.mgsx.gltf.scene3d.shaders.PBRShaderProvider;
import net.mgsx.gltf.scene3d.utils.IBLBuilder;

// Utility class to render a game object to generate an icon sprite.  These icons are used in the inventory.

public class ShowCase implements Disposable {
    final static int SHADOW_MAP_SIZE = 2048;

    private SceneManager sceneManager;
    private OrthographicCamera camera;
    private Cubemap diffuseCubemap;
    private Cubemap environmentCubemap;
    private Cubemap specularCubemap;
    private Texture brdfLUT;
    private DirectionalLightEx light;
    private DirectionalShadowLight shadowCastingLight;
    private FrameBuffer fbo;
    private Filter filter;

    public ShowCase() {
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
        camera.zoom = 0.025f;
        camera.up.set(Vector3.Y);
        camera.lookAt( new Vector3(0, 0f, 0));

        camera.update();
        sceneManager.setCamera(camera);



        // setup light

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

        sceneManager.setAmbientLight(0.1f);
        sceneManager.environment.set(new PBRTextureAttribute(PBRTextureAttribute.BRDFLUTTexture, brdfLUT));
        sceneManager.environment.set(PBRCubemapAttribute.createSpecularEnv(specularCubemap));
        sceneManager.environment.set(PBRCubemapAttribute.createDiffuseEnv(diffuseCubemap));

        filter = new Filter();
    }

    public Sprite makeIcon(SceneAsset asset, int w, int h, boolean high ){
        // asset can be null to get an empty icon
        Scene scene = null;
        if(asset != null) {
            scene = new Scene(asset.scene);
            sceneManager.addScene(scene);
        }

        if(high)
            camera.lookAt( new Vector3(0, 1f, 0));
        else
            camera.lookAt( new Vector3(0, 0f, 0));
        camera.update();

        fbo = new FrameBuffer(Pixmap.Format.RGBA8888, w, h, true);

        sceneManager.updateViewport(w, h);
        filter.resize(w, h);

        // render
        sceneManager.renderShadows();
        fbo.begin();
        ScreenUtils.clear(Color.BLACK, true);
        sceneManager.update(0.1f);
        sceneManager.renderColors();
        fbo.end();


        //do we need to make a copy of the texture?
        Sprite sprite = new Sprite(fbo.getColorBufferTexture());
        sprite.flip(false, true); // coordinate system in buffer differs from screen

        //filter.render(fbo,0,0, w, h);

        if(scene != null)
            sceneManager.removeScene(scene);

        return sprite;
    }

    @Override
    public void dispose() {
        sceneManager.dispose();
        environmentCubemap.dispose();
        diffuseCubemap.dispose();
        specularCubemap.dispose();
        brdfLUT.dispose();
        filter.dispose();
        fbo.dispose();
    }
}
