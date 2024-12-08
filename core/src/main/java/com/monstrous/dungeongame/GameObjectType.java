package com.monstrous.dungeongame;

public class GameObjectType {
    String name;
    boolean character;
    boolean pickup;

    public GameObjectType(String name, boolean character, boolean pickup) {
        this.name = name;
        this.character = character;
        this.pickup = pickup;
    }
}
