package com.monstrous.dungeongame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class GUI implements Disposable {

    public Stage stage;
    private Skin skin;
    private Label level;
    private Label gold;
    private Label hp;
    private Label xp;
    private Label message1, message2, message3;
    private StringBuffer sb;
    private World world;

    public GUI( World world ) {
        this.world = world;
        skin = new Skin(Gdx.files.internal("blue-pixel-skin/blue-pixel.json"));
        stage = new Stage(new ScreenViewport());
        sb = new StringBuffer();

        // rely on resize() to call rebuild()
    }

    private void rebuild(){
        Gdx.app.log("GUI", "rebuild");
        stage.clear();





        Table uiPanel = new Table();


        level = new Label("LEVEL: 0", skin);
        gold = new Label("GOLD: 0", skin);
        hp = new Label("HP: 0", skin);
        xp = new Label("XP: 0", skin);
        //gold.setColor(Color.GOLD);
        uiPanel.add(level).left().top().expandX();
        uiPanel.row();
        uiPanel.add(gold).left().top().expandX();
        uiPanel.row();
        uiPanel.add(hp).left().top().expandX();
        uiPanel.row();
        uiPanel.add(xp).left().top().expandX();
        uiPanel.row();
        message1 = new Label("..", skin);
        message2 = new Label("..", skin);
        message3 = new Label("..", skin);
        // message.setColor(Color.BLUE);
        Table messageBox = new Table();
        messageBox.align(Align.left);
        messageBox.add(message1).left();
        messageBox.row();
        messageBox.add(message2).left();
        messageBox.row();
        messageBox.add(message3).left();

        uiPanel.add(messageBox).top().left().expand();
        uiPanel.pack();

        // Screen is split in 2 columns. Left for 3d view and Right for fixed width ui panel
        Table screenTable = new Table();
        screenTable.setFillParent(true);

        screenTable.add().expand();         // empty column
        screenTable.add(uiPanel).width(300).top().left();
        screenTable.pack();

        stage.addActor(screenTable);
    }


    private void update(){
        sb.setLength(0);
        sb.append("LEVEL: ");
        sb.append(world.level);
        level.setText(sb.toString());

        sb.setLength(0);
        sb.append("GOLD: ");
        sb.append(world.rogue.stats.gold);
        gold.setText(sb.toString());

        sb.setLength(0);
        sb.append("HP: ");
        sb.append(world.rogue.stats.hitPoints);
        hp.setText(sb.toString());

        sb.setLength(0);
        sb.append("XP: ");
        sb.append(world.rogue.stats.experience);
        xp.setText(sb.toString());

        message3.setText(MessageBox.lines.get(MessageBox.lines.size-1));
        message2.setText(MessageBox.lines.get(MessageBox.lines.size-2));
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
