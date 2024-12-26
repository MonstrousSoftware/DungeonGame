package com.monstrous.dungeongame;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.environment.PointLight;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import net.mgsx.gltf.scene3d.lights.PointLightEx;

public class TorchLights {

    private int roomId;
    private Array<PointLightEx> lights;
    private float time;

    public TorchLights(Environment environment) {
        roomId = -1;
        lights = new Array<>();
        for(int i = 0; i < DungeonScenes.MAX_TORCHES; i++){
            PointLightEx pointLight = new PointLightEx();
            pointLight.position.set(0,1,0);
            pointLight.color.set(Color.ORANGE);
            pointLight.range = 0.5f;
            pointLight.intensity = 0f;  // off by default
            lights.add( pointLight );
            environment.add( pointLight );
        }
    }

    public void update(float delta, Room room){
        if(room == null && roomId == -1)
            return;
        if(room != null && room.id == roomId) {
            flicker(delta);
            return;
        }
        roomId = (room == null ? -1 : room.id);
        for(PointLightEx light : lights) {
            light.intensity = 0;
        }
        if(room == null)
            return;

        int index = 0;
        for(Vector3 pos : room.torchPositions){
            PointLightEx pointLight = lights.get(index);
            index++;
            pointLight.position.set(pos);
            pointLight.intensity = 2f;
        }
    }

    private void flicker(float delta ){
        time += delta;
        for(int index = 0; index < lights.size; index++){
            PointLightEx light = lights.get(index);
            if(light.intensity > 0)
                light.intensity = 3f - 0.5f* (float)Math.sin(index + 7*time )*(float)Math.cos(11f*time);
        }
    }
}
