package com.monstrous.dungeongame;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends Game {

    public World world;
    public Music music;


    @Override
    public void create() {
        world = new World();

        music = Gdx.audio.newMusic(Gdx.files.internal("music/the-cave-220274.mp3"));
        music.setLooping(true);
        music.play();

        setScreen(new GameScreen(this));
    }
}
