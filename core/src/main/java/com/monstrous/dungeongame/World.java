package com.monstrous.dungeongame;


// World contains the static dungeon architecture: map
// and the items and enemies within it: gameObjects

import com.badlogic.gdx.math.MathUtils;

public class World {
    private final static int MAP_WIDTH = 30;
    private final static int MAP_HEIGHT = 20;
    public final static int DELTA_WIDTH = 6;
    public final static int DELTA_HEIGHT = 4;

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
        level--;
        map.dispose();
        generateLevel();
    }

    public void restart(){
        MessageBox.clear();
        seed = MathUtils.random(1,9999);
        rogue = null;
        level = 0;
        map.dispose();
        generateLevel();
        MessageBox.addLine("World seed: "+seed);
    }

    private void generateLevel(){
        isRebuilt = true;
        // map gets bigger at lower levels: keep aspect ratio 3/2
        // todo match up stairs
        int w = MAP_WIDTH+DELTA_WIDTH*level;
        int h = MAP_HEIGHT+DELTA_HEIGHT*level;
        map = new DungeonMap(seed, level, w, h);

        gameObjects = new GameObjects(w, h);
        enemies = new Enemies(this);
        // add dynamic object to the gameObjects list and its occupants grid

        if(rogue == null)   // don't create new rogue when changing level
            rogue = Populator.placeRogue(map, gameObjects);

        Populator.distributeGoodies(map, gameObjects);
        Populator.distributeEnemies(map, gameObjects, enemies);

        gameObjects.clearOccupant(rogue.x, rogue.y);

    }
}
