package com.monstrous.dungeongame.gui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.monstrous.dungeongame.GameObjectType;
import com.monstrous.dungeongame.GameObjectTypes;
import com.monstrous.dungeongame.Inventory;
import com.monstrous.dungeongame.World;


// one slot in the inventory
// gets a reference to the corresponding Inventory.Slot an uses this to keep the visual
// representation up to date when update() is called.
//
public class InventorySlotButton extends Button {

    public static final Color BACKGROUND_COLOUR = Color.DARK_GRAY;

    private final Label countLabel;
    private Inventory.Slot slot;
    private Image image;
    private TextureRegionDrawable placeHolder;
    private GameObjectType currentType;
    private int count;
    private World world;

    public InventorySlotButton(String text, Skin skin, final World world, final Inventory.Slot slot) {
        super(skin, "slot");
        this.world = world;
        this.slot = slot;
        count = 0;

        Table countTable = new Table();
        countLabel = new Label("", skin, "small");
        countTable.add(countLabel).right().bottom().expandX().expandY();    // small number in bottom right hand side, we use the table to position the label
        countTable.pack();

        currentType = null;
        placeHolder = makePlaceHolder();        // for empty slot
        image = new Image(placeHolder);         // will be updated by update()

        // make a stack of layers: the icon image and on top of that the counter value

        Stack stack = new Stack();
        stack.add(image);
        stack.add(countTable);
        add(stack).pad(3);

//        addListener(new ClickListener() {
//            @Override
//            public void clicked(InputEvent event, float x, float y) {
//                super.clicked(event, x, y);
//                GameObjectType type = slot.removeItem();
//                world.takeFromInventory(type);
//                adjustCountIndicator();
//            }
//        });
    }


    // return true if slot contents has just changed
    public boolean update() {

        boolean updated = false;

        if(slot.count != this.count) {
            adjustCountIndicator();
            this.count = slot.count;
            updated = true;
        }

        // has the type changed? (type null means empty slot)
        if(slot.count == 0 && this.currentType != null){
            image.setDrawable(placeHolder);
            this.currentType = null;
            updated = true;
        }
        else if( slot.count > 0 && slot.object.type != this.currentType) {
            image.setDrawable(new TextureRegionDrawable(slot.object.type.icon));
            this.currentType = slot.object.type;
            updated = true;
        }
        return updated;
    }

    private void adjustCountIndicator() {
        if(slot.count <= 0)
            countLabel.setText("");         // don't show a zero
        else if(!slot.object.type.isCountable)
            countLabel.setText("");         // don't show a 1 for uncountables
        else
            countLabel.setText(slot.count);
    }

    private TextureRegionDrawable makePlaceHolder() {
        int size = GameObjectTypes.ICON_SIZE;
        Pixmap pixmap = new Pixmap(size, size, Pixmap.Format.RGBA8888);
        pixmap.setColor(BACKGROUND_COLOUR);
        pixmap.fill();
        return new TextureRegionDrawable(new Texture(pixmap));
    }
}
