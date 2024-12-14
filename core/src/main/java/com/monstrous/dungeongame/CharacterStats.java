package com.monstrous.dungeongame;

public class CharacterStats {
    public int equipped;
    public int hitPoints;
    public int strength;
    public int armour;
    public int experience;
    public int gold;
    public GameObjectType armourItem;   // todo object rather than type
    public Inventory inventory;

    public CharacterStats() {
        equipped = 0;
        hitPoints = 5;
        strength = 10;
        armour = 0;
        experience = 0;
        gold = 0;
        armourItem = null;
        inventory = new Inventory();
    }
}
