package com.monstrous.dungeongame;

import com.badlogic.gdx.utils.Array;

public class GameObjectTypes {
    public static GameObjectType rogue;
    public static GameObjectType gold;
    public static GameObjectType knife;
    public static GameObjectType crossbow;

    public static Array<GameObjectType> types;

    public GameObjectTypes() {
        types = new Array<>();

        rogue = new GameObjectType("Rogue", true, false);
        types.add(rogue);
        gold = new GameObjectType("Gold", false, true);
        types.add(gold);
        knife = new GameObjectType("Knife", false, true);
        types.add(knife);
        crossbow = new GameObjectType("Crossbow", false, true);
        types.add(crossbow);
     }
}
