package com.monstrous.dungeongame;

import com.badlogic.gdx.Gdx;

public class Inventory {

    public  final static int NUM_SLOTS = 10;

    public Slot[] slots;

    public class Slot {
        public GameObject object;
        public int count;

        public Slot() {
            object = null;
            count = 0;
        }

        // may return null if slot was empty
        public GameObject removeItem() {
            if(count <= 0)
                 return null;
            count--;
            GameObject item;
            if(count == 0) {
                item = object;
                object = null;
            }
            else
                item = new GameObject(object.type, 0, 0, Direction.NORTH);
            return item;
        }

        public boolean isEmpty(){
            return count == 0;
        }

        public void addItem(GameObject item){
            if(object == null){
                assert count == 0;
                object = item;
            }
            count++;
        }
    }


    public Inventory() {
        slots = new Slot[NUM_SLOTS];
        for(int i = 0 ; i < NUM_SLOTS;i++)
            slots[i] = new Slot();
    }

    public boolean addItem(GameObject item) {

        GameObjectType type = item.type;

        // find slot of matching type if this is a fungible item such as gold
        if(type.isCountable) {
            for (int i = 0; i < NUM_SLOTS; i++) {
                if (!slots[i].isEmpty() && slots[i].object.type == type) {
                    slots[i].addItem(item);
                    Gdx.app.log("Inventory", "slot " + i + " type: " + type.name + " count:" + slots[i].count);
                    return true;
                }
            }
        }
        // find first free slot
        for(int i = 0; i < NUM_SLOTS; i++) {
            if(slots[i].isEmpty() ){
                slots[i].addItem(item);
                Gdx.app.log("Inventory", "slot "+i+" type: "+type.name+" count:"+slots[i].count);
                return true;
            }
        }
        Gdx.app.error("inventory full", "");
        return false;
    }

    public GameObject removeItem(int slot) {
        assert slot >= 0 && slot < NUM_SLOTS;
        return slots[slot].removeItem();
    }
}
