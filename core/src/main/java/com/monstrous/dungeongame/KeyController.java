package com.monstrous.dungeongame;

import com.badlogic.gdx.InputAdapter;
import net.mgsx.gltf.scene3d.scene.Scene;

public class KeyController extends InputAdapter {

    private DungeonMap map;
    private DungeonScenes scenes;

    public KeyController(DungeonMap map, DungeonScenes scenes) {
        this.map = map;
        this.scenes = scenes;
    }

    @Override
    public boolean keyTyped(char character) {
        switch(character){
            case 'w':   moveRogue(0, -1); return true;
            case 'a':   moveRogue(-1, 0); return true;
            case 's':   moveRogue(0, 1); return true;
            case 'd':   moveRogue(1,0); return true;
            default:    return false;
        }
    }

    private void moveRogue(int dx, int dy){
        Scene rogue = scenes.getRogue();
        rogue.modelInstance.transform.translate(4*dx, 0, 4*dy);

    }
}
