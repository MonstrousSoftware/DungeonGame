package com.monstrous.dungeongame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;

public class Enemies {
    public Array<GameObject> enemies;
    private World world;


    public Enemies(World world) {
        this.world = world;
        enemies = new Array<>();
    }

    public void add(GameObject enemy){
        enemies.add(enemy);
    }

    public void remove(GameObject enemy){
        enemies.removeValue(enemy, true);
    }

    public void clear(){
        enemies.clear();
    }

    public void step(DungeonScenes scenes){
        for(GameObject enemy : enemies){
            enemy.step(world, scenes);
        }
    }
}
