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
    public int initXP;
    public float z;     // height to render at when place on the ground (to avoid some model being inside the floor)

    public GameObjectType(String name, boolean character, boolean pickup) {
        this.name = name;
        this.character = character;
        this.pickup = pickup;
        this.isPlayer = false;
        this.isCountable = false;
        this.initXP = 1;
        this.z = 0f;
    }
}
