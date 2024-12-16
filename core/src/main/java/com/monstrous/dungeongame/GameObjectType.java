package com.monstrous.dungeongame;

import com.badlogic.gdx.graphics.g2d.Sprite;
import net.mgsx.gltf.scene3d.scene.SceneAsset;

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
    public boolean isGold;
    public boolean isArrow;
    public boolean isSpellBook;
    public GameObjectType alternative;
    public SceneAsset sceneAsset;
    public int initProtection;      // for armour
    public int initDamage;
    public int initAccuracy;
    public int initXP;
    public boolean initAggressive;
    public float z;     // height to render at when place on the ground (to avoid some model being inside the floor)
    public Sprite icon;

    public GameObjectType( String name, boolean character, boolean pickup) {
        this.name = name;
        this.character = character;
        this.pickup = pickup;
        this.isPlayer = false;
        this.isCountable = false;
        this.initXP = 1;
        this.z = 1f;
        this.isWeapon = false;
        this.isArmour = false;
        this.isEdible = false;
        this.isPotion = false;
        this.isGold = false;
        this.isArrow = false;
        this.isSpellBook = false;
        this.initProtection = 0;
        this.initDamage = 0;
        this.initAccuracy = 0;
        this.initAggressive = false;
    }
}
