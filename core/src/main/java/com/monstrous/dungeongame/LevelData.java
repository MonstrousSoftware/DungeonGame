package com.monstrous.dungeongame;

import com.badlogic.gdx.utils.Array;


// persistent data per dungeon level that cannot be generated from the seed.

public class LevelData {
    public int level;                       // level nr
    public Array<Room> stairPortals;        // stairs to connect to next level.
    public boolean[][] tileSeen;            // has the corridor segment been seen?
    public Array<Integer> seenRooms;        // room ids of rooms that were uncovered
    public GameObjects gameObjects;

    public LevelData(int levelNr, int w, int h) {
        this.level = levelNr;
        this.stairPortals = new Array<>();
        this.seenRooms = new Array<>();
        this.tileSeen = new boolean[h][w];
        this.gameObjects = new GameObjects(w, h);
    }
}
