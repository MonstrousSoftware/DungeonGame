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
        if(world.rogue.stats.hitPoints <= 0) {
            if(character == 'R'){
                restart();
                return true;
            }
            return false;
        }

        boolean handled = processKey(character);
        if(handled)
            world.enemies.step(scenes);
        if(world.rogue.stats.hitPoints <= 0){
            MessageBox.addLine("You are dead. Press Shift-R to restart.");
        }

        return handled;
    }

    private boolean processKey(char character) {
        switch(character){
            // left/right keys translate to -x/+x
            // up/down to +y/-y
            //
            case 'w':   tryMoveRogue(0, 1, Direction.NORTH); return true;
            case 'a':   tryMoveRogue(-1, 0, Direction.WEST); return true;
            case 's':   tryMoveRogue(0, -1, Direction.SOUTH); return true;
            case 'd':   tryMoveRogue(1,0, Direction.EAST); return true;
            case '0':   equip( Equipped.NONE ); return true;
            case '1':   equip( Equipped.KNIFE ); return true;
            case '2':   equip( Equipped.THROWABLE ); return true;
            case '3':   equip( Equipped.CROSSBOW ); return true;
            case 'p':   dropGold(); return true;
            case 'R':   restart(); return true;
            case ' ':   return true;        // do nothing
            default:    return false;
        }
    }

    private void restart(){
        world.restart();
        scenes.clear();
        scenes.placeRogue( world );
        int roomId = world.map.roomCode[world.rogue.y][world.rogue.x];
        Room room = world.map.rooms.get(roomId);
        scenes.buildRoom( world.map, room );
        scenes.populateRoom(world, room);
    }

    private void tryMoveRogue(int dx, int dy, Direction dir){
        int x = world.rogue.x;
        int y = world.rogue.y;

        scenes.turnObject( world.rogue, dir, x, y);
        x += dx;
        y += dy;
        TileType cell = world.map.getGrid(x, y);
        if(!TileType.walkable(cell))
            return;

        GameObject occupant = world.gameObjects.getOccupant(x, y);

        if(occupant != null && occupant.type.isEnemy){
            fight(occupant);
            return; // don't move into the target cell
        }

//        world.gameObjects.clearOccupant( world.rogue.x, world.rogue.y);
//        world.gameObjects.setOccupant(x, y, world.rogue);
        scenes.moveObject( world.rogue, x, y, world.rogue.z);

        if(occupant != null && occupant.type.pickup){
            Gdx.app.log("Pickup", occupant.type.name);
                                                    // assumes gold
            MessageBox.addLine("You picked up "+occupant.quantity+" "+occupant.type.name);

            if(occupant.scene != null)
                scenes.remove(occupant.scene);
            world.gameObjects.clearOccupant(x, y);
            if(occupant.type == GameObjectTypes.gold){
                world.rogue.stats.gold += occupant.quantity;
            }
        }

        // show the room if this is the first time we enter it
        int roomId = world.map.roomCode[y][x];
        if(roomId >= 0) {
            Room room = world.map.rooms.get(roomId);
            if (!room.uncovered) {
                scenes.buildRoom(world.map, room);
                scenes.populateRoom(world, room);
            }
        } else if( world.map.getGrid(x,y) == TileType.CORRIDOR){
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

    }


    private void fight(GameObject enemy){
        enemy.stats.hitPoints -= 1;
        enemy.attackedBy = world.rogue;
        MessageBox.addLine("You hit the "+enemy.type.name+"(HP: "+enemy.stats.hitPoints+")");
        if(enemy.stats.hitPoints <= 0){
            defeat(enemy);
        }
    }

    private void defeat(GameObject enemy){
        MessageBox.addLine("You have defeated the "+enemy.type.name+". (XP +1)");
        scenes.remove(enemy.scene);
        world.gameObjects.clearOccupant(enemy.x, enemy.y);
        world.rogue.stats.experience++;
        if(enemy.stats.gold > 0) {
            MessageBox.addLine("You have take their gold. (+"+enemy.stats.gold+")");
            world.rogue.stats.gold += enemy.stats.gold;
        }
    }



    // testing
    private void dropGold(){
        if(world.rogue.stats.gold == 0) {
            MessageBox.addLine("You have no gold to drop.");
            return;
        }
        MessageBox.addLine("You dropped 1 gold.");

        world.rogue.stats.gold--;
        GameObject gold = scenes.placeObject(world.gameObjects, GameObjectTypes.gold, world.rogue.x, world.rogue.y);
        gold.quantity = 1;
    }

    private void equip( int equipped ){
        world.rogue.stats.equipped = equipped;
        scenes.adaptModel(world.rogue.scene, equipped);
    }
}
