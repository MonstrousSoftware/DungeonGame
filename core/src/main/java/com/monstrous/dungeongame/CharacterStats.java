package com.monstrous.dungeongame;

public class CharacterStats {
    //public int equipped;
    public int hitPoints;
    public int strength;
    public int armour;
    public int experience;
    public int gold;
    public GameObject armourItem;
    public GameObject weaponItem;
    public Inventory inventory;

    public CharacterStats() {
        //equipped = 0;
        hitPoints = 5;
        strength = 10;
        armour = 0;
        experience = 0;
        gold = 0;
        weaponItem = null;
        armourItem = null;
        inventory = new Inventory();
    }
}
