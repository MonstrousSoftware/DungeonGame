package com.monstrous.dungeongame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class HelpScreen  extends ScreenAdapter {

    private Main game;
    private Skin skin;
    private Stage stage;

    public HelpScreen(Main game) {
        this.game = game;
    }

    @Override
    public void show() {

        skin = new Skin(Gdx.files.internal("skin/d3.json"));
        stage = new Stage(new ScreenViewport());

        Table screenTable = new Table();
        screenTable.setFillParent(true);

        Label title = new Label("Desperately Dangerous Dungeons", skin);
        screenTable.add(title).pad(30).center();
        screenTable.row();


        Label keyLabel = new Label("Keys:", skin, "small");
        keyLabel.setText("KEYS:\n  Arrow keys to move\n  SPACE : rest\n  U + digit : use\n  D + digit : drop\n  E + digit : equip\n  Z, C : turn\n  M : map\n  R : restart\n");
        screenTable.add(keyLabel).center();
        screenTable.row();

        screenTable.add(new Label("Press H to return", skin, "small")).pad(20).center();
        screenTable.pack();

        stage.addActor(screenTable);


        //stage.addActor(new Label("Press H to return", skin));
    }

    @Override
    public void render(float deltaTime) {
        if(Gdx.input.isKeyJustPressed(Input.Keys.H)){
            game.setScreen( new GameScreen(game) );
            return;
        }

        ScreenUtils.clear(Color.BLACK);

        stage.act(deltaTime);
        stage.draw();

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