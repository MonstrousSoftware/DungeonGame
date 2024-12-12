package com.monstrous.dungeongame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;

public class Sounds {
    private static Sound pickupSound;
    private static Sound fightSound;
    private static Sound monsterDeathSound;


    public static void pickup(){
        if(pickupSound == null)
            pickupSound = Gdx.audio.newSound(Gdx.files.internal("sounds/item-equip-6904.mp3"));
        pickupSound.play();
    }

    public static void fight(){
        if(fightSound == null)
            fightSound = Gdx.audio.newSound(Gdx.files.internal("sounds/punch_h_05-224063.mp3"));
        fightSound.play();
    }

    public static void monsterDeath(){
        if(monsterDeathSound == null)
            monsterDeathSound = Gdx.audio.newSound(Gdx.files.internal("sounds/monster-death-grunt-131480.mp3"));
        monsterDeathSound.play();
    }


}
