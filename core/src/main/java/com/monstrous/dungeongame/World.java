package com.monstrous.dungeongame;


// World contains the static dungeon architecture: map
// and the items and enemies within it: gameObjects

import com.badlogic.gdx.math.MathUtils;

public class World {
    public final static int MAP_WIDTH = 30;
    public final static int MAP_HEIGHT = 30;

    public int seed = 1234;
    public int level = 0;

    public DungeonMap map;
    public GameObjects gameObjects;
    public GameObject rogue;
    public Enemies enemies;
    public boolean isRebuilt;

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

    public void restart(){
        MessageBox.clear();
        seed = MathUtils.random(1,9999);
        level = 0;
        map.dispose();
        generateLevel();
        MessageBox.addLine("World seed: "+seed);
    }

    private void generateLevel(){
        isRebuilt = true;
        map = new DungeonMap(seed, level, MAP_WIDTH, MAP_HEIGHT);

        gameObjects = new GameObjects(MAP_WIDTH, MAP_HEIGHT);
        enemies = new Enemies(this);
        // add dynamic object to the gameObjects list and its occupants grid

        rogue = Populator.placeRogue(map, gameObjects);         // todo not when moving between level;
        Populator.distributeGold(map, gameObjects);
        Populator.distributeEnemies(map, gameObjects, enemies);

        gameObjects.clearOccupant(rogue.x, rogue.y);

    }
}
