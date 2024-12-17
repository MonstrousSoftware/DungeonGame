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

    public final static String VERSION = "version 1.1 16/12/2024";

    private final String[] tips = {
        "Take your time to think. It's a turn based game and there is no need to rush.",
        "Don't forget to eat or you may faint and need to skip some turns.",
        "Health regenerates over time. Press SPACE to rest.",
        "The monsters at lower levels are more dangerous.",
        "Experience (XP) will improve your fighting.",
        "Imps like to steal gold.",
        "Warriors get aggressive when they are in a bad mood.",
        "Monsters will also pick up items and fight each other when you're not around.",
        "Armour has a protection level that can degrade in a fight.",
        "You can swap degraded weapons by equipping better ones",
        "Poisonous potions can be thrown at enemies.",
        "Throwing arrows do more damage when a crossbow is equipped.",
        "Weapons have damage level and accuracy level.",
        "Once opened, a spell book is useless.",
        "Remember what the different potions do.",
        "You can use the scroll wheel to zoom. ",
        "Increase awareness lets you know what the monsters do.",
        "Blocked attacks degrade weapon accuracy.",
        "Unlock the map (M) by finding the Book of Maps.",
        "The Sword of Yobled is at dungeon level 5 or below."
    };

    private final Main game;
    private Skin skin;
    private Stage stage;
    private Label tipLabel;
    private int tipIndex = 0;

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
        title.setColor(Color.GOLD);
        screenTable.add(title).pad(20).center();
        screenTable.row();
        Label version = new Label(VERSION, skin, "smaller");
        screenTable.add(version).pad(10).center();
        screenTable.row();

        Label storyLabel = new Label("", skin, "small");
        storyLabel.setText("As the final task of your apprenticeship to become\na professional Rogue, you have been sent into "+
        "these\ndangerous dungeons to retrieve the Sword of Yobled.\nIt is rumoured to be at least five levels below ground level.\n");
        screenTable.add(storyLabel).pad(20).center();
        screenTable.row();



        Label keyLabel = new Label("Keys:", skin, "small");
        keyLabel.setColor(Color.CYAN);
        keyLabel.setText("KEYS:\n  Arrow keys to move\n  SPACE : rest\n  U + digit : use\n  D + digit : drop\n  E + digit : equip\n  T+digit+arrow : throw\n  Z, C : turn\n  M : map\n  R : restart\n");
        screenTable.add(keyLabel).pad(20).center();
        screenTable.row();

        screenTable.add(new Label("Press T for a Game Tip", skin, "small")).pad(10).center();
        screenTable.row();
        screenTable.add(new Label("Press C for Credits", skin, "small")).pad(10).center();
        screenTable.row();
        screenTable.add(new Label("Press H to return", skin, "small")).pad(10).center();
        screenTable.row();


        tipLabel = new Label("", skin, "small");
        tipLabel.setColor(Color.GOLD);
        screenTable.add(tipLabel).pad(20).center();
        screenTable.pack();

        stage.addActor(screenTable);
    }

    @Override
    public void render(float deltaTime) {
        if(Gdx.input.isKeyJustPressed(Input.Keys.C)){
            game.setScreen( new CreditsScreen(game) );
            return;
        }
        if(Gdx.input.isKeyJustPressed(Input.Keys.H)){
            game.setScreen( new GameScreen(game) );
            return;
        }
        if(Gdx.input.isKeyJustPressed(Input.Keys.T)){
            tipLabel.setText(tips[tipIndex]);
            tipIndex = (tipIndex + 1) % tips.length;
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
