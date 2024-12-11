package com.monstrous.dungeongame;


// World contains the static dungeon architecture: map
// and the items and enemies within it: gameObjects

public class World {
    public final static int MAP_WIDTH = 30;
    public final static int MAP_HEIGHT = 30;

    public int seed = 1234;
    public int level = 1;

    public DungeonMap map;
    public GameObjects gameObjects;
    public GameObject rogue;
    public Enemies enemies;

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
        enemies = new Enemies(this);
        // add dynamic object to the gameObjects list and its occupants grid

        rogue = Populator.placeRogue(map, gameObjects);
        Populator.distributeGold(map, gameObjects);
        Populator.distributeEnemies(map, gameObjects, enemies);

    }
}
