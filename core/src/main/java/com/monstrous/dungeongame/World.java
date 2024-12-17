package com.monstrous.dungeongame;


// World contains the static dungeon architecture: map
// and the items and enemies within it: gameObjects

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;

public class World {
    private final static int MAP_WIDTH = 30;
    private final static int MAP_HEIGHT = 20;
    public final static int DELTA_WIDTH = 6;
    public final static int DELTA_HEIGHT = 4;

    public int seed = 1234;
    public int level = 0;
    public int swordLevel;

    public DungeonMap map;
    public GameObjects gameObjects;
    public GameObject rogue;
    public Enemies enemies;
    public boolean isRebuilt;
    public float secondsElapsed;
    public Array<LevelData> levelDataArray;
    private LevelData levelData;


    public World() {
        secondsElapsed = 0;
        GameObjectTypes gameObjectTypes = new GameObjectTypes();
        levelDataArray = new Array<>();
        create();
    }

    private void create(){
        MessageBox.clear();
        rogue = null;
        level = 0;
        secondsElapsed = 0;
        levelDataArray.clear();
        randomizeSwordLevel();
        generateLevel();
    }

    public void levelDown(){
        level++;        // top level is 0, lower levels have higher numbers
        map.dispose();
        generateLevel();
    }

    public void levelUp(){
        if(level == 0)
            return;
        level--;
        map.dispose();
        generateLevel();
        if(level == 0 && rogue.stats.inventory.contains(GameObjectTypes.bigSword)) {
            MessageBox.addLine("Congratulations!");
            MessageBox.addLine("The Sword of Yobled was recovered.");
            MessageBox.addLine("You have finished the game!");
            // todo more fanfare
        }
    }

    public void restart(){
        map.dispose();
        seed = MathUtils.random(1,9999);
        MessageBox.addLine("World seed: "+seed);
        create();
    }



    private void randomizeSwordLevel(){
        MathUtils.random.setSeed(seed);
        // set level where the sword can be found
        swordLevel = 5 + MathUtils.random(0,2);
    }

    private void generateLevel(){
        isRebuilt = true;

        if(level > levelDataArray.size-1) {       // new level
            levelData = new LevelData(level);
            levelDataArray.add(levelData);
        } else {    // existing level
            levelData = levelDataArray.get(level);
        }
        Array<Room> stairsFromAbove;
        if(level == 0)
            stairsFromAbove = new Array<>();
        else
            stairsFromAbove = levelDataArray.get(level-1).stairPortals;


        // map gets bigger at lower levels: keep aspect ratio 3/2
        //
        int w = MAP_WIDTH+DELTA_WIDTH*level;
        int h = MAP_HEIGHT+DELTA_HEIGHT*level;
        map = new DungeonMap(seed, level, w, h, stairsFromAbove, levelData.stairPortals);

        gameObjects = new GameObjects(w, h);
        enemies = new Enemies(this);
        // add dynamic object to the gameObjects list and its occupants grid

        if(rogue == null)   // don't create new rogue when changing level
            rogue = Populator.placeRogue(map, gameObjects);

        if(level == swordLevel)
            Populator.placeSword(map, gameObjects);
        Populator.distributeGoodies(map, gameObjects);
        Populator.distributeEnemies(map, level, gameObjects, enemies);

        gameObjects.clearOccupant(rogue.x, rogue.y);

    }
}
