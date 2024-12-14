package com.monstrous.dungeongame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;

public class KeyController extends InputAdapter {

    private World world;
    private DungeonScenes scenes;
    private boolean equipMode;
    private boolean dropMode;
    private boolean useMode;
    private boolean confirmMode;
    private int frozenTimer;

    public KeyController(World world, DungeonScenes scenes) {
        this.world = world;
        this.scenes = scenes;
        equipMode = false;
        dropMode = false;
        useMode = false;
    }

    @Override
    public boolean keyDown(int keycode) {
        // left/right keys translate to -x/+x
        // up/down to +y/-y
        //
        if(!preAction())
            return true;
        boolean done = false;
        switch(keycode){
            case Input.Keys.LEFT:
                tryMoveRogue(-1, 0, Direction.WEST);
                done = true;
                break;
            case Input.Keys.RIGHT:
                tryMoveRogue(1, 0, Direction.EAST);
                done = true;
                break;
            case Input.Keys.UP:
                tryMoveRogue(0, 1, Direction.NORTH);
                done = true;
                break;
            case Input.Keys.DOWN:
                tryMoveRogue(0, -1, Direction.SOUTH);
                done = true;
                break;
        }
        if(done)
            wrapUp();
        return done;
    }

    @Override
    public boolean keyTyped(char character) {
        if (world.rogue.stats.hitPoints <= 0) {
            if (character == 'R') {
                restart();
                return true;
            }
            return false;
        }
        if(!preAction())
            return true;

        boolean handled = processKey(character);
        if (handled)
            wrapUp();

        return handled;
    }

    private boolean preAction(){
        if (world.rogue.stats.hitPoints <= 0) {
            return false;
        }
        if(frozenTimer > 0){    // still frozen?
            frozenTimer--;
            return false;
        }
        return true;
    }

    // arrive here after having made a move
    private void wrapUp(){
        world.enemies.step(scenes);     // move enemies
        digestFood();
        if (world.rogue.stats.hitPoints <= 0) {
            MessageBox.addLine("You are dead. Press Shift-R to restart.");
        }
    }

    private void digestFood(){
        System.out.println("food: "+world.rogue.stats.food);
        world.rogue.stats.food -= 2;
        if(world.rogue.stats.food == 20)
            MessageBox.addLine("You feel hungry.");
        else if(world.rogue.stats.food == 6)
            MessageBox.addLine("You're so hungry you feel faint.");
        else if(world.rogue.stats.food == 0){
            MessageBox.addLine("You're so faint you can't move.");
            frozenTimer = 5;
            world.rogue.stats.food = 30;
        }
    }

    private boolean processKey(char character) {
        if(equipMode)
            return processEquipChoice(character);
        if(dropMode)
            return processDropChoice(character);
        if(useMode)
            return processUseChoice(character);
        if(confirmMode)
            return processConfirmation(character);

        switch (character) {

            case 'z':
                turnRogue(false); return true;
            case 'c':
                turnRogue(true); return true;
            case 'e':
                equip();
                return true;
            case 'd':
                drop();
                return true;
            case 'u':
                use();
                return true;
            case 'r':
                confirmMode = true;
                MessageBox.addLine("Confirm with Y to restart.");
                return true;
            case ' ':
                return true;        // do nothing
            default:
                return false;
        }
    }

    private void restart() {
        world.restart();
        scenes.clear();
        scenes.liftFog(world);
        int roomId = world.map.roomCode[world.rogue.y][world.rogue.x];
        Room room = world.map.rooms.get(roomId);
        scenes.buildRoom(world.map, room);
        scenes.populateRoom(world, room);
    }

    private void turnRogue(boolean clockWise) {
        int dir = world.rogue.direction.ordinal();
        if(clockWise)
            dir = (dir + 1) % 4;
        else
            dir = (dir+3) % 4;

        scenes.turnObject(world.rogue, Direction.values()[dir], world.rogue.x, world.rogue.y);    // turn towards moving direction
    }

    private void tryMoveRogue(int dx, int dy, Direction dir){
        // if on bottom of stairs and moving forward, move down a level
        if(world.map.getGrid(world.rogue.x,world.rogue.y) == TileType.STAIRS_DOWN_DEEP &&
            dir == world.map.tileOrientation[world.rogue.y][world.rogue.x]){
            world.levelDown();
            // continue to make the move off the bottom step
        }
        else if(world.map.getGrid(world.rogue.x,world.rogue.y) == TileType.STAIRS_UP_HIGH &&
            dir == Direction.opposite(world.map.tileOrientation[world.rogue.y][world.rogue.x])){
            world.levelUp();
            // continue to make the move off the top step
        }

        world.rogue.tryMove(world, scenes, dx, dy, dir);

        int x = world.rogue.x;
        int y = world.rogue.y;
        // show the room if this is the first time we enter it
        int roomId = world.map.roomCode[y][x];

        Gdx.app.log("Rogue on tile", world.map.getGrid(x,y).toString());
        if(roomId >= 0) {

            Room room = world.map.rooms.get(roomId);
            if (!room.uncovered) {
                Gdx.app.log("Uncover room", ""+roomId);
                scenes.buildRoom(world.map, room);
                scenes.populateRoom(world, room);
            }
        } else if( world.map.getGrid(x,y) == TileType.CORRIDOR){
            if(!world.map.corridorSeen[y][x])
                Gdx.app.log("Uncover corridor", " "+x+", "+y);
            scenes.visitCorridorSegment(world.map, x, y);
        }
        if( world.map.getGrid(x,y) == TileType.STAIRS_DOWN){
            world.rogue.z = -2;
        }
        else if( world.map.getGrid(x,y) == TileType.STAIRS_DOWN_DEEP){
            world.rogue.z = -6;
        }
        else if( world.map.getGrid(x,y) == TileType.STAIRS_UP){
            world.rogue.z = 2;
        }
        else if( world.map.getGrid(x,y) == TileType.STAIRS_UP_HIGH){
            world.rogue.z = 6;
        }
        else {
            world.rogue.z = 0;
        }
        scenes.moveObject( world.rogue, x, y, world.rogue.z);
    }




    private void equip(){
        MessageBox.addLine("Equip what? (0-9) or Esc");
        equipMode = true;
    }

    private boolean processEquipChoice(int character){
        equipMode = false;
        if(character >= '0' && character <= '9'){
            equipSlot(character - '0');
            return true;
        }
        return false;
    }

    private boolean processConfirmation(int character){
        confirmMode = false;

        if(character ==  'y' ||  character == 'Y'){
            restart();
        }
        return false;
    }

//    private void read(){
//        MessageBox.addLine("Read what? (0-9) or Esc");
//        readMode = true;
//
//    }
//
//    private boolean processReadChoice(int character){
//        readMode = false;
//        if(character >= '0' && character <= '9'){
//            readSlot(character - '0');
//            return true;
//        }
//        return false;
//    }

    private void drop(){
        MessageBox.addLine("Drop what? (0-9) or Esc");
        dropMode = true;
    }

    private boolean processDropChoice(int character){
        dropMode = false;
        if(character >= '0' && character <= '9'){
            dropSlot(character - '0');
            return true;
        }
        return false;
    }

    private void use(){
        MessageBox.addLine("Use what? (0-9) or Esc");
        useMode = true;
    }

    private boolean processUseChoice(int character){
        useMode = false;
        if(character >= '0' && character <= '9'){
            useSlot(character - '0');
            return true;
        }
        return false;
    }

    private void equipSlot(int slotNr ){
        Inventory.Slot slot = world.rogue.stats.inventory.slots[slotNr];
        if(slot.isEmpty())
            return;
        if(slot.object.type.isArmour){
            GameObject prev = world.rogue.stats.armourItem;
            world.rogue.stats.armourItem = slot.removeItem();
            if(prev != null)
                world.rogue.stats.inventory.addItem(prev);
        } else if(slot.object.type.isWeapon){
            GameObject prev = world.rogue.stats.weaponItem;
            world.rogue.stats.weaponItem = slot.removeItem();
            if(prev != null)
                world.rogue.stats.inventory.addItem(prev);
        }
        scenes.adaptModel(world.rogue.scene, world.rogue.stats);

    }

//    private void readSlot(int slotNr ){
//        Inventory.Slot slot = world.rogue.stats.inventory.slots[slotNr];
//        if(slot.isEmpty())
//            return;
//        if(slot.object.type == GameObjectTypes.spellBookClosed) {
//            slot.object.type = GameObjectTypes.spellBookOpen;
//            readSpell();
//        } else if(slot.object.type == GameObjectTypes.spellBookOpen) {
//            MessageBox.addLine("Can only read spell book once.");
//        } else {
//            MessageBox.addLine("Can't read "+slot.object.type.name+".");
//        }
//    }

    private void dropSlot(int slotNr ){
        Inventory.Slot slot = world.rogue.stats.inventory.slots[slotNr];
        if(slot.isEmpty())
            return;
        GameObject item = slot.removeItem();
        MessageBox.addLine("You dropped "+item.type.name+".");
        scenes.placeObject(world.gameObjects, item.type, world.rogue.x, world.rogue.y);
    }


    private void useSlot(int slotNr ){
        Inventory.Slot slot = world.rogue.stats.inventory.slots[slotNr];
        if(slot.isEmpty())
            return;
        if(slot.object.type.isEdible) {
            GameObject item = slot.removeItem();
            MessageBox.addLine("You eat the food.");
            world.rogue.stats.food = CharacterStats.MAX_FOOD;
        } else if(slot.object.type.isPotion) {
            GameObject potion = slot.removeItem();
            drinkPotion(potion);
        } else if(slot.object.type == GameObjectTypes.spellBookClosed) {
            slot.object.type = GameObjectTypes.spellBookOpen;
            readSpell();
        } else if(slot.object.type == GameObjectTypes.spellBookOpen) {
            MessageBox.addLine("Can only read spell book once.");
        } else {
            MessageBox.addLine("Can't use "+slot.object.type.name+".");
        }
    }




    private void readSpell(){
        MessageBox.addLine("You read the spell book and you feel sad.");
    }

    private void drinkPotion(GameObject potion){
        MessageBox.addLine("You drink the "+potion.type.name+".");
        // todo some effect
    }
}
