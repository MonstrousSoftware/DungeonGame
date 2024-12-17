package com.monstrous.dungeongame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;

public class KeyController extends InputAdapter {

    private World world;
    private DungeonScenes scenes;
    private boolean equipMode;  // after e
    private boolean dropMode;   // after d
    private boolean useMode;    // after u
    private boolean confirmMode;        // after reset
    private boolean throwMode;  // after t
    private boolean throwDirectionMode; // after t + item
    private int throwSlot;
    private int frozenTimer;
    private int regenTimer;
    private int digestionSpeed = 2;

    public KeyController(World world, DungeonScenes scenes) {
        this.world = world;
        this.scenes = scenes;
        equipMode = false;
        dropMode = false;
        useMode = false;
        confirmMode = false;
        throwMode = false;
        throwDirectionMode = false;
        regenTimer = 10;
    }

    @Override
    public boolean keyDown(int keycode) {

        // left/right keys translate to -x/+x
        // up/down to +y/-y
        //

        if (world.rogue.stats.hitPoints <= 0) { // player is dead
            return false;
        }

        if(throwDirectionMode) {
            processThrowDirectionChoice(keycode);
            return true;
        }


        boolean done = true;
        if(frozenTimer > 0){    // still frozen?
            frozenTimer--;
        }
        else {
            done = false;
            switch (keycode) {
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
        }
        if(done)
            wrapUp();
        return done;
    }

    @Override
    public boolean keyTyped(char character) {
        //System.out.println("keytyped: "+character);
        // if player is dead, only accept restart command
        if (world.rogue.stats.hitPoints <= 0) {
            if (character == 'r') {
                restart();
                return true;
            }
            return false;
        }

        boolean handled = true;
        if(frozenTimer > 0){    // still frozen?
            frozenTimer--;
        }
        else {
            handled = processKey(character);
        }
        if (handled)
            wrapUp();

        return handled;
    }


    // arrive here after having made a move or skipping a turn
    private void wrapUp(){
        if(world.rogue.stats.increasedAwareness > 0){
            world.rogue.stats.increasedAwareness--;
            if(world.rogue.stats.increasedAwareness == 0)
                MessageBox.addLine("Your increased awareness wore off.");
        }
        if(--regenTimer <= 0){
            regenTimer = Math.max(20-world.level, 3);
            if(world.rogue.stats.hitPoints < CharacterStats.MAX_HITPOINTS)
                world.rogue.stats.hitPoints++;
        }
        world.enemies.step(scenes);     // move enemies

        digestFood();

        // check for death
        if (world.rogue.stats.hitPoints <= 0) {
            MessageBox.addLine("You are dead. Press R to restart.");
            world.rogue.scene.animationController.setAnimation(null);   // remove previous animation
            world.rogue.scene.animationController.setAnimation("Death_A", 1);
        }
    }

    private void digestFood(){
        //System.out.println("food: "+world.rogue.stats.food);
        world.rogue.stats.food -= digestionSpeed;
        if(world.rogue.stats.food == 20) {
            Sounds.stomachRumble();
            MessageBox.addLine("You feel hungry.");
        }
        else if(world.rogue.stats.food == 6) {
            Sounds.stomachRumble();
            MessageBox.addLine("You're so hungry you feel faint.");
        }
        else if(world.rogue.stats.food == 0){
            Sounds.stomachRumble();
            MessageBox.addLine("You're so faint you can't move.");
            world.rogue.scene.animationController.setAnimation(null);   // remove previous animation
            world.rogue.scene.animationController.setAnimation("Lie_Idle", 3);
            frozenTimer = 5;
            world.rogue.stats.food = CharacterStats.REPLENISH_FOOD;
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
        if(throwMode)
            return processThrowChoice(character);

        //System.out.println("Character: "+character);
        switch (Character.toLowerCase(character)) {

            case 'z':
                turnRogue(false); return true;
            case 'c':
                turnRogue(true); return true;
            case 'e':
                equip();
                return false;
            case 'd':
                drop();
                return false;
            case 'u':
                use();
                return false;
            case 't':
                throwItem();
                return false;       // return false because it's not enemy's move yet
            case 'r':
                confirmMode = true;
                MessageBox.addLine("Confirm with Y to restart.");
                return false;
            case ' ':
                world.rogue.scene.animationController.setAnimation(null);   // remove previous animation
                world.rogue.scene.animationController.setAnimation("Idle", 3);
                return true;        // do nothing
            case '#':   // cheat code
                world.rogue.stats.haveBookOfMaps = true;
                return true;
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
        scenes.showRoom(world.map, room);
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

        //Gdx.app.log("Rogue on tile", world.map.getGrid(x,y).toString());
        if(roomId >= 0) {

            Room room = world.map.rooms.get(roomId);
            if (!room.uncovered) {
                Gdx.app.log("Uncover room", ""+roomId);
                scenes.showRoom(world.map, room);
                scenes.populateRoom(world, room);
            }
        } else if( world.map.getGrid(x,y) == TileType.CORRIDOR){
            if(!world.map.tileSeen[y][x])
                Gdx.app.log("Uncover corridor", " "+x+", "+y);
            scenes.visitCorridorSegment(world, x, y);
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
            equipSlot(slotNumber(character));
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

    private void drop(){
        MessageBox.addLine("Drop what? (0-9) or Esc");
        dropMode = true;
    }

    private boolean processDropChoice(int character){
        dropMode = false;
        if(character >= '0' && character <= '9'){
            dropSlot(slotNumber(character));
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
            useSlot(slotNumber(character));
            return true;
        }
        return false;
    }



    private void throwItem(){
        MessageBox.addLine("Throw what? (0-9) or Esc");
        throwMode = true;
    }

    private boolean processThrowChoice(int character){
        throwMode = false;
        if(character >= '0' && character <= '9'){
            throwSlot = slotNumber(character);
            // ask direction
            MessageBox.addLine("Which direction? (arrow keys)");
            throwDirectionMode = true;
            return false;       // return false because it's not the enemy's move yet
        }
        return false;
    }

    // after t + item + direction
    private boolean processThrowDirectionChoice(int keycode){
        throwDirectionMode = false;
        switch(keycode){
            case Input.Keys.LEFT:
                throwIt(throwSlot, -1, 0, Direction.WEST);
                break;
            case Input.Keys.RIGHT:
                throwIt(throwSlot, 1, 0, Direction.EAST);
                break;
            case Input.Keys.UP:
                throwIt(throwSlot, 0, 1, Direction.NORTH);
                break;
            case Input.Keys.DOWN:
                throwIt(throwSlot, 0, -1, Direction.SOUTH);
                break;
        }
        return false;
    }

    private boolean throwIt(int slotNr, int dx, int dy, Direction dir){
        System.out.println("Throw "+slotNr+" to dx:"+dx+", dy:"+dy);

        world.rogue.scene.animationController.setAnimation(null);   // remove previous animation
        world.rogue.scene.animationController.setAnimation("Throw", 1);

        Inventory.Slot slot = world.rogue.stats.inventory.slots[slotNr];
        if(slot.isEmpty())
            return true;
        // turn rogue in direction of throw
        scenes.turnObject(world.rogue, dir, world.rogue.x, world.rogue.y);
        // take item from inventory slot
        GameObject item = slot.removeItem();
        MessageBox.addLine("You throw "+item.type.name+".");
        if(item.type.isGold)
            world.rogue.stats.gold -= item.quantity;
        int tx = world.rogue.x;
        int ty = world.rogue.y;
        while(true) {
            // next tile
            int nx = tx + dx;
            int ny = ty + dy;
            if(nx < 0 || nx > world.map.mapWidth || ny < 0 || ny > world.map.mapHeight)
                return true;

            GameObject occupant = world.gameObjects.getOccupant(nx, ny);
            if(occupant != null && occupant.type.isEnemy){
                MessageBox.addLine("You hit "+occupant.type.name+".");
                item.hits(world, scenes, world.rogue, occupant);
                return true;
            }
            // move as long as we are over floor or corridor
            // i.e. don't go through walls, but you can throw through doorways
            TileType tile = world.map.getGrid(nx, ny);
            if(!TileType.walkable(tile,  world.map.getGrid(tx, ty))) {
                // drop item short of the wall
                scenes.dropObject(world.map, world.gameObjects, item, tx, ty);
                return true;
            }
            tx = nx;
            ty = ny;
        }
    }

    private int slotNumber(int k){
        return ((k-'0')+9) % 10;    // '1', '2', '3' maps to 0,1,2
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

    private void dropSlot(int slotNr ){
        Inventory.Slot slot = world.rogue.stats.inventory.slots[slotNr];
        if(slot.isEmpty())
            return;
        GameObject item = slot.removeItem();
        if(item.type.isGold)
            world.rogue.stats.gold -= item.quantity;
        MessageBox.addLine("You dropped "+item.type.name+".");
        scenes.dropObject(world.map, world.gameObjects, item, world.rogue.x, world.rogue.y);
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
        } else if(slot.object.type.isSpellBook) {
            readSpell(slot.object.type);
            slot.object.type = slot.object.type.alternative;

        } else if(slot.object.type == GameObjectTypes.spellBookOpen) {
            MessageBox.addLine("Can only read spell book once.");
        } else {
            MessageBox.addLine("Can't use "+slot.object.type.name+".");
        }
    }

    private void readSpell(GameObjectType type){
        MessageBox.addLine("You read the spell book.");
        if(type == GameObjectTypes.spellBookClosed) {
            MessageBox.addLine("The paper makes you feel sad.");
        } else if(type == GameObjectTypes.spellBookClosedB) {
            MessageBox.addLine("It is a book of maps.");
            world.rogue.stats.haveBookOfMaps = true;
        } else if(type == GameObjectTypes.spellBookClosedC) {
            if(world.level == world.swordLevel)
                MessageBox.addLine("The Sword of Yobled is at this level.");
            else
                MessageBox.addLine("The Sword of Yobled is not at this level.");
        } else if(type == GameObjectTypes.spellBookClosedD) {
            MessageBox.addLine("It has no effect.");
        }
    }

    private void drinkPotion(GameObject potion){
        MessageBox.addLine("You drink the "+potion.type.name+".");
        if(potion.type == GameObjectTypes.bottle_A_brown){
            world.rogue.stats.increasedAwareness = 100;
            MessageBox.addLine("Your awareness is increased.");
        } else if(potion.type == GameObjectTypes.bottle_C_green){
            world.rogue.stats.hitPoints = Math.max(0, world.rogue.stats.hitPoints-3);
            MessageBox.addLine("It is poison. You lose health.");
        } else if(potion.type == GameObjectTypes.bottle_B_green){
            world.rogue.stats.hitPoints = Math.max(CharacterStats.MAX_HITPOINTS, world.rogue.stats.hitPoints+3);
            MessageBox.addLine("You feel invigorated.");
        } else if(potion.type == GameObjectTypes.bottle_A_green) {
            digestionSpeed = 1;
            MessageBox.addLine("This aids your digestion.");
        } else {
            MessageBox.addLine("It has no effect.");
        }
        // todo some effect
    }
}
