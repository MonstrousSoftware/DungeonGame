package com.monstrous.dungeongame;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends Game {

    public World world;
    public KeyController keyController; // so it can be shared between screens
    public Music music;
    private MessageBox mb = new MessageBox();


    @Override
    public void create() {
        //world = new World();

        MessageBox.addLine("Welcome traveller!");

        music = Gdx.audio.newMusic(Gdx.files.internal("music/the-cave-220274.mp3"));
        music.setLooping(true);
        music.play();

        setScreen(new PreGameScreen(this));
    }
}
