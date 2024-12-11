package com.monstrous.dungeongame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
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
    private Label message1, message2, message3;
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


        gold = new Label("GOLD: 0", skin);
        //gold.setColor(Color.GOLD);
        screenTable.add(gold).pad(10).left().top().expandX();
        screenTable.row();
        message1 = new Label("..", skin);
        message2 = new Label("..", skin);
        message3 = new Label("..", skin);
        // message.setColor(Color.BLUE);
        Table messageBox = new Table();
        messageBox.add(message1);
        messageBox.row();
        messageBox.add(message2);
        messageBox.row();
        messageBox.add(message3);

        screenTable.add(messageBox).top().left().expand();
        screenTable.pack();
        stage.addActor(screenTable);
    }


    private void update(){
        sb.setLength(0);
        sb.append("GOLD: ");
        sb.append(rogue.goldQuantity);
        gold.setText(sb.toString());

        if(MessageBox.lines.size > 0)
            message3.setText(MessageBox.lines.get(MessageBox.lines.size-1));
        if(MessageBox.lines.size > 1)
            message2.setText(MessageBox.lines.get(MessageBox.lines.size-2));
        if(MessageBox.lines.size > 2)
            message1.setText(MessageBox.lines.get(MessageBox.lines.size-3));
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
