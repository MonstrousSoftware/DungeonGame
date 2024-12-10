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
            default:    return false;
        }
    }

    private void tryMoveRogue(int dx, int dy, Direction dir){
        int x = scenes.rogue.x;
        int y = scenes.rogue.y;

        scenes.turnRogue(world.map, dir, x, y);
        x += dx;
        y += dy;
        TileType cell = world.map.getGrid(x, y);
        if(walkable(cell)) {
            GameObject occupant = world.gameObjects.getOccupant(x, y);
            if(occupant != null){
                Gdx.app.log("occupant", occupant.type.name);
                if(occupant.type.pickup){
                    Gdx.app.log("Pickup", occupant.type.name);
                                                            // assumes gold
                    MessageBox.addLine("You picked up "+occupant.goldQuantity+" "+occupant.type.name);

                    if(occupant.scene != null)
                        scenes.remove(occupant.scene);
                    world.gameObjects.clearOccupant(x, y);
                    if(occupant.type == GameObjectTypes.gold){
                        scenes.getRogue().goldQuantity += occupant.goldQuantity;
                    }
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
                scenes.rogue.z = -2;
            }
            else if( world.map.getGrid(x,y) == TileType.STAIRS_DOWN_DEEP){
                scenes.rogue.z = -6;
            }
            else {
                scenes.rogue.z = 0;
            }

            scenes.moveRogue( x, y, scenes.rogue.z);
        }
    }

    // testing
    private void dropGold(){
        GameObject rogue = scenes.getRogue();
        if(rogue.goldQuantity == 0) {
            MessageBox.addLine("You have no gold to drop.");
            return;
        }
        MessageBox.addLine("You dropped 1 gold.");

        rogue.goldQuantity--;
        GameObject gold = scenes.placeObject(world.gameObjects, GameObjectTypes.gold, rogue.x, rogue.y);
        gold.goldQuantity = 1;
    }

    private boolean walkable(TileType cell){
        switch(cell){
            case ROOM:
            case CORRIDOR:
            case DOORWAY:
            case STAIRS_DOWN:
            case STAIRS_DOWN_DEEP:
                return true;
            default:
                return false;
        }
    }

    private void equip( int equipped ){
        scenes.getRogue().equipped = equipped;
        scenes.adaptModel(scenes.getRogue().scene, equipped);
    }
}
