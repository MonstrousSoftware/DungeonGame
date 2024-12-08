package com.monstrous.dungeongame;

public class World {
    public final static int MAP_WIDTH = 80;
    public final static int MAP_HEIGHT = 60;

    public int seed = 1234;

    public DungeonMap map;
    public GameObjects gameObjects;

    public World() {
        map = new DungeonMap(seed, 0, MAP_WIDTH, MAP_HEIGHT);
        GameObjectTypes gameObjectTypes = new GameObjectTypes();
        gameObjects = new GameObjects(MAP_WIDTH, MAP_HEIGHT);
    }
}
