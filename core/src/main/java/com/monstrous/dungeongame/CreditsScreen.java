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

public class CreditsScreen extends ScreenAdapter {

    private Main game;
    private Skin skin;
    private Stage stage;

    public CreditsScreen(Main game) {
        this.game = game;
    }

    @Override
    public void show() {

        skin = new Skin(Gdx.files.internal("skin/d3.json"));
        stage = new Stage(new ScreenViewport());

        Table screenTable = new Table();
        screenTable.setFillParent(true);

        Label title = new Label("About Desperately Dangerous Dungeons", skin);
        title.setColor(Color.GOLD);
        screenTable.add(title).pad(20).center();
        screenTable.row();


        Label textLabel = new Label("", skin, "small");

        textLabel.setText("This is an entry in LibGDX Game Jam #31 " +
            "(December 2024) with the theme 'roguelike'.\n" +
            "by Monstrous Software\n\n" +
            "It is inspired by the original Rogue game (1980)" +
            " by Michael Toy, Glenn Wichman and later " +
            "contributions by Ken Arnold.\n" +
            "\n" +
            "3D models:\n" +
            "KayKit - Character Pack Adventurers (via itch.io) (CC0 license)\n" +
            "KayKit - Dungeon Pack Remastered (via itch.io) (CC0 license)\n" +
            "KayKit - Skeletons Character Pack (via itch.io) (CC0 license)\n" +
            "\n" +
            "Music:\n" +
            "\"The Cave\" by Andrea Good (LuminousPresence) via Pixabay (Pixabay content license)  \n" +
            "\n" +
            "Font:\n" +
            "Metamorphous by James Grieshaber via Google fonts (Open Font License)\n" +
            "\n" +
            "Sound effects:\n" +
            "Various sound effects via Pixabay");


        screenTable.add(textLabel).pad(20).center();
        screenTable.row();

        screenTable.add(new Label("Press ENTER to return", skin, "small")).pad(20).center();
        screenTable.pack();

        stage.addActor(screenTable);
    }

    @Override
    public void render(float deltaTime) {
        if(Gdx.input.isKeyJustPressed(Input.Keys.ENTER)){
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
