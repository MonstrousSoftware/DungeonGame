package com.monstrous.dungeongame;

public class CharacterStats {
    public final static int MAX_HITPOINTS = 10;
    public final static int MAX_FOOD = 500;
    public final static int REPLENISH_FOOD = 100;   // after a faint

    public int hitPoints;
    public int experience;
    public int gold;
    public int food;
    public boolean aggressive;
    public GameObject armourItem;
    public GameObject weaponItem;
    public Inventory inventory;
    public int increasedAwareness;  // reports on off-screen events if > 0, wears off
    public boolean haveBookOfMaps;

    public CharacterStats() {
        hitPoints = 5;
        experience = 0;
        gold = 0;
        food = MAX_FOOD;
        aggressive = false;
        weaponItem = null;
        armourItem = null;
        inventory = new Inventory(10);
        increasedAwareness = 0;
        haveBookOfMaps = false;
    }
}
