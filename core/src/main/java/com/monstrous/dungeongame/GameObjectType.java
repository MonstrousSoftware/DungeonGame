package com.monstrous.dungeongame;

import com.badlogic.gdx.graphics.g2d.Sprite;
import net.mgsx.gltf.scene3d.scene.SceneAsset;
import com.badlogic.gdx.graphics.Texture;

public class GameObjectType {
    public String name;
    public boolean character;
    public boolean pickup;
    public boolean isPlayer;
    public boolean isEnemy;
    public boolean isCountable;
    public boolean isWeapon;
    public boolean isArmour;
    public boolean isEdible;
    public boolean isPotion;
    public SceneAsset sceneAsset;
    public int initXP;
    public float z;     // height to render at when place on the ground (to avoid some model being inside the floor)
    public Sprite icon;

    public GameObjectType(String name, boolean character, boolean pickup) {
        this.name = name;
        this.character = character;
        this.pickup = pickup;
        this.isPlayer = false;
        this.isCountable = false;
        this.initXP = 1;
        this.z = 0f;
        this.isWeapon = false;
        this.isArmour = false;
        this.isEdible = false;
        this.isPotion = false;
    }
}
