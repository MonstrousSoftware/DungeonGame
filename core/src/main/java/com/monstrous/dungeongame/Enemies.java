package com.monstrous.dungeongame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;

public class Enemies {
    public Array<GameObject> enemies;


    public Enemies() {
        enemies = new Array<>();
    }

    public void add(GameObject enemy){
        enemies.add(enemy);
    }

    public void addFromObjects(GameObjects gameObjects)
    {
        for(GameObject go : gameObjects.gameObjects ){
            if(go.type.isEnemy)
                enemies.add(go);
        }
    }

    public void remove(GameObject enemy){
        enemies.removeValue(enemy, true);
    }

    public void clear(){
        enemies.clear();
    }

    public void step(World world, DungeonScenes scenes){
        for(GameObject enemy : enemies){
            enemy.step(world, scenes);
        }
    }

    public void hideAll(DungeonScenes scenes){
        for(GameObject enemy : enemies){
            if(enemy.scene != null)
                scenes.remove(enemy.scene);
        }
    }
}
