package com.monstrous.dungeongame;

import com.badlogic.gdx.utils.Array;


// persistent data per dungeon level that cannot be generated from the seed.

public class LevelData {
    public int level;
    public Array<Room> stairPortals;       // stairs to connect to next level.

    public LevelData(int levelNr) {
        this.level = levelNr;
        this.stairPortals = new Array<>();
    }
}
