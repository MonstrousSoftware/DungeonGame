package com.monstrous.dungeongame;

import com.badlogic.gdx.utils.Array;

public class GameObjects {
    public Array<GameObject> gameObjects;

    private GameObject[][] occupant;

    public GameObjects(int width, int height) {
        gameObjects = new Array<>();
        occupant = new GameObject[height][width];
    }

    public void add(GameObject go){
        gameObjects.add(go);
    }

    // may be null
    public GameObject getOccupant(int x, int y){
        return occupant[y][x];
    }

    public void setOccupant(int x, int y, GameObject go){
        occupant[y][x] = go;
    }

    public void clearOccupant(int x, int y){
        occupant[y][x] = null;
    }
}
