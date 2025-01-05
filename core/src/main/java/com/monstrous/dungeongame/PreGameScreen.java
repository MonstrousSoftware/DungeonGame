package com.monstrous.dungeongame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class PreGameScreen extends ScreenAdapter {

    private Main game;
    private Skin skin;
    private Stage stage;
    private float time;

    public PreGameScreen(Main game) {
        this.game = game;
    }

    @Override
    public void show() {

        skin = new Skin(Gdx.files.internal("skin/d3.json"));
        stage = new Stage(new ScreenViewport());

        Table screenTable = new Table();
        screenTable.setFillParent(true);

        screenTable.add(new Label("Loading...", skin, "small")).pad(20).center();
        screenTable.pack();

        stage.addActor(screenTable);
        time = 0;
    }

    @Override
    public void render(float deltaTime) {
        time += deltaTime;

        ScreenUtils.clear(Color.BLACK);
        stage.act(deltaTime);
        stage.draw();

        if(time > 0.1f){
            if(game.world == null)
                game.world = new World();

            game.setScreen( new GameScreen(game) );
            return;
        }

    }

    @Override
    public void resize(int width, int height) {
    }


    @Override
    public void hide() {
        // This method is called when another screen replaces this one.
    }

    @Override
    public void dispose() {
        // Destroy screen's assets here.
        skin.dispose();
    }
}
