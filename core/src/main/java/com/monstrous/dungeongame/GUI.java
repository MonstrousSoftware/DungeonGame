package com.monstrous.dungeongame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class GUI implements Disposable {

    public Stage stage;
    private Skin skin;
    private Label gold;
    private StringBuffer sb;
    private GameObject rogue;

    public GUI( GameObject rogue ) {
        this.rogue = rogue;
        skin = new Skin(Gdx.files.internal("blue-pixel-skin/blue-pixel.json"));
        stage = new Stage(new ScreenViewport());
        sb = new StringBuffer();

        // rely on resize() to call rebuild()
    }

    private void rebuild(){
        Gdx.app.log("GUI", "rebuild");
        stage.clear();

        Table screenTable = new Table();
        screenTable.setFillParent(true);

        screenTable.add(new Label("GOLD:", skin)).pad(10).top().left();
        gold = new Label("0", skin);
        screenTable.add(gold).pad(10).left().top().expand();
        screenTable.pack();
        stage.addActor(screenTable);
    }


    private void update(){
        sb.setLength(0);
        sb.append(rogue.gold);
        gold.setText(sb.toString());
    }


    public void render(float deltaTime) {
        update();

        stage.act(deltaTime);
        stage.draw();
    }

    public void resize(int width, int height) {
        Gdx.app.log("GUI resize", "gui " + width + " x " + height);
        stage.getViewport().update(width, height, true);
        rebuild();
    }


    @Override
    public void dispose() {
        skin.dispose();
    };
}
