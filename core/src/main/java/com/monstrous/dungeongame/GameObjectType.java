package com.monstrous.dungeongame;

import net.mgsx.gltf.scene3d.scene.SceneAsset;

public class GameObjectType {
    public String name;
    public boolean character;
    public boolean pickup;
    public boolean isPlayer;
    public boolean isEnemy;
    public boolean isCountable;
    public SceneAsset sceneAsset;

    public GameObjectType(String name, boolean character, boolean pickup) {
        this.name = name;
        this.character = character;
        this.pickup = pickup;
        this.isPlayer = false;
        this.isCountable = false;
    }
}
