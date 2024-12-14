package com.monstrous.dungeongame.gui;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.monstrous.dungeongame.Inventory;
import com.monstrous.dungeongame.World;

public class InventoryWindow extends Window {

    private InventorySlotButton[] buttons;
    private int numSlots;

    public InventoryWindow(String title, Skin skin, World world) {
        super(title, skin, "grey-canvas");
        Inventory inventory = world.rogue.stats.inventory;

        numSlots = inventory.NUM_SLOTS;

        buttons = new InventorySlotButton[numSlots];

        Table gridTable = new Table();

        int index = 0;
        for(int x = 0; x < numSlots; x++) {
            InventorySlotButton b = new InventorySlotButton("TEST", skin, world, index);
            buttons[index++] = b;
            gridTable.add(b);
        }
        gridTable.row();
        for(int x = 0; x < numSlots; x++) {
            gridTable.add(new Label(""+x, skin, "small"));
        }
        add(gridTable);
        pack();
    }

    public void update() {
        for(int i = 0; i < numSlots; i++){
            buttons[i].update();
        }
    }
}
