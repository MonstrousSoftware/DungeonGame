package com.monstrous.dungeongame.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.monstrous.dungeongame.*;

public class GUI implements Disposable {

    public static final int PANEL_WIDTH = 400;      // width of UI panel

    public Stage stage;
    private Skin skin;
    private Label level;
    private Label gold;
    private Label hp;
    private Label xp;
    private Image weapon, armour;
    private Label message1, message2, message3;
    private StringBuffer sb;
    private World world;
    private GameObject equippedWeapon;
    private GameObject equippedArmour;
    private InventoryWindow inventoryWindow;

    public GUI( World world ) {
        this.world = world;
//        skin = new Skin(Gdx.files.internal("blue-pixel-skin/blue-pixel.json"));
        skin = new Skin(Gdx.files.internal("skin/d3.json"));
        stage = new Stage(new ScreenViewport());
        sb = new StringBuffer();

        // rely on resize() to call rebuild()

        inventoryWindow = new InventoryWindow("Inventory", skin, world);

    }

    private void rebuild(){
        Gdx.app.log("GUI", "rebuild");
        stage.clear();

        stage.addActor(inventoryWindow);



        Table uiPanel = new Table();


        level = new Label("DUNGEON LEVEL: 0", skin, "small");
        gold = new Label("GOLD: 0", skin,"small");
        hp = new Label("HP: 0", skin,"small");
        xp = new Label("XP: 0", skin,"small");
        //gold.setColor(Color.GOLD);
        uiPanel.add(level).padLeft(8).padTop(10).left().top().expandX();
        uiPanel.row();
        uiPanel.add(gold).padLeft(8).left().top().expandX();
        uiPanel.row();
        uiPanel.add(hp).padLeft(8).left().top().expandX();
        uiPanel.row();
        uiPanel.add(xp).padLeft(8).left().top().expandX();
        uiPanel.row();

        Table eq = new Table();
        weapon = new Image();
        TextureRegion region = new TextureRegion(GameObjectTypes.emptyIcon);
        region.flip(false, true);
        weapon.setDrawable(new TextureRegionDrawable(region));
        eq.add(weapon).pad(5).right().top();

        armour = new Image();
        region = new TextureRegion(GameObjectTypes.emptyIcon);
        region.flip(false, true);
        armour.setDrawable(new TextureRegionDrawable(region));
        eq.add(armour).pad(5).left().top();
        eq.pack();

        uiPanel.add(eq).center();
        uiPanel.row();

        message1 = new Label("..", skin, "smaller");
        message2 = new Label("..", skin, "smaller");
        message3 = new Label("..", skin,"smaller");
        // message.setColor(Color.BLUE);
        Table messageBox = new Table();
        messageBox.add(message1).pad(3).left();
        messageBox.row();
        messageBox.add(message2).pad(3).left();
        messageBox.row();
        messageBox.add(message3).pad(3).left();

        uiPanel.add(messageBox).pad(10).top().left().expand();
        uiPanel.pack();

        // Screen is split in 2 columns. Left for 3d view and Right for fixed width ui panel
        Table screenTable = new Table();
        screenTable.setFillParent(true);

        screenTable.add().expand();         // empty column
        screenTable.add(uiPanel).width(PANEL_WIDTH).top().left();
        screenTable.pack();

        stage.addActor(screenTable);
    }


    private void update(){
        sb.setLength(0);
        sb.append("DUNGEON LEVEL: ");
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

        setWeapon();
        setArmour();
        inventoryWindow.update();
    }

    private void setWeapon(){
        if(world.rogue.stats.weaponItem != equippedWeapon){
            equippedWeapon = world.rogue.stats.weaponItem;

            Sprite icon = null;
            if(equippedWeapon == null)
                icon = GameObjectTypes.emptyIcon;
            else
                icon = equippedWeapon.type.icon;

            TextureRegion region = new TextureRegion(icon.getTexture());
            region.flip(false, true);
            weapon.setDrawable(new TextureRegionDrawable(region));
        }
    }

    private void setArmour(){
        if(world.rogue.stats.armourItem != equippedArmour){
            equippedArmour = world.rogue.stats.armourItem;

            Sprite icon = null;
            if(equippedArmour == null)
                icon = GameObjectTypes.emptyIcon;
            else
                icon = equippedArmour.type.icon;

            TextureRegion region = new TextureRegion(icon.getTexture());
            region.flip(false, true);
            armour.setDrawable(new TextureRegionDrawable(region));
        }
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
