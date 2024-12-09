package com.monstrous.dungeongame;


// World contains the static dungeon architecture: map
// and the items and enemies within it: gameObjects

public class World {
    public final static int MAP_WIDTH = 80;
    public final static int MAP_HEIGHT = 60;

    public int seed = 1234;
    public int level = 0;

    public DungeonMap map;
    public GameObjects gameObjects;

    public World() {
        GameObjectTypes gameObjectTypes = new GameObjectTypes();
        generateLevel();
    }

    public void levelDown(){
        level++;        // top level is 0, lower levels have higher numbers
        map.dispose();
        generateLevel();
    }

    public void levelUp(){
        level++;
        map.dispose();
        generateLevel();
    }

    private void generateLevel(){
        map = new DungeonMap(seed, level, MAP_WIDTH, MAP_HEIGHT);

        gameObjects = new GameObjects(MAP_WIDTH, MAP_HEIGHT);
        Populator.distributeGold(map, gameObjects);
        Populator.placeRogue(map, gameObjects);
    }
}
