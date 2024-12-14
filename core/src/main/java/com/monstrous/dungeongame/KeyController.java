package com.monstrous.dungeongame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;

public class KeyController extends InputAdapter {

    private World world;
    private DungeonScenes scenes;

    public KeyController(World world, DungeonScenes scenes) {
        this.world = world;
        this.scenes = scenes;
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

        boolean handled = processKey(character);
        if (handled)
            world.enemies.step(scenes);
        if (world.rogue.stats.hitPoints <= 0) {
            MessageBox.addLine("You are dead. Press Shift-R to restart.");
        }

        return handled;
    }

    private boolean processKey(char character) {
        switch (character) {
            // left/right keys translate to -x/+x
            // up/down to +y/-y
            //
            case 'w':
                tryMoveRogue(0, 1, Direction.NORTH);
                return true;
            case 'a':
                tryMoveRogue(-1, 0, Direction.WEST);
                return true;
            case 's':
                tryMoveRogue(0, -1, Direction.SOUTH);
                return true;
            case 'd':
                tryMoveRogue(1, 0, Direction.EAST);
                return true;
            case 'q':
                turnRogue(false); return true;
            case 'e':
                turnRogue(true); return true;
            case '0':
                equip(Equipped.NONE);
                return true;
            case '1':
                equip(Equipped.KNIFE);
                return true;
            case '2':
                equip(Equipped.THROWABLE);
                return true;
            case '3':
                equip(Equipped.CROSSBOW);
                return true;
            case '5':
                useArmour(null);
                return true;
            case '6':
                useArmour(GameObjectTypes.shield1);
                return true;
            case '7':
                useArmour(GameObjectTypes.shield2);
                return true;
            case 'p':
                dropGold();
                return true;
            case 'R':
                restart();
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


    // testing
    private void dropGold(){
        if(world.rogue.stats.gold == 0) {
            MessageBox.addLine("You have no gold to drop.");
            return;
        }
        GameObject occupant = world.gameObjects.getOccupant(world.rogue.x, world.rogue.y);
        if(occupant != null && occupant.type == GameObjectTypes.gold) {
            MessageBox.addLine("You dropped 1 gold.");
            // if there is already gold, add to it
            world.rogue.stats.gold--;
            occupant.quantity++;
        } else if( occupant != null){
            MessageBox.addLine("Cannot drop here.");
        } else {
            MessageBox.addLine("You dropped 1 gold.");
            GameObject gold = scenes.placeObject(world.gameObjects, GameObjectTypes.gold, world.rogue.x, world.rogue.y);
            gold.quantity = 1;
        }
    }

    private void equip( int equipped ){
        world.rogue.stats.equipped = equipped;
        scenes.adaptModel(world.rogue.scene, equipped);
    }

    private void useArmour( GameObjectType type ){
        world.rogue.stats.armourItem = type;
    }
}
