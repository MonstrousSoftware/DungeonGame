package com.monstrous.dungeongame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;

public class KeyController extends InputAdapter {

    private DungeonMap map;
    private DungeonScenes scenes;
    private GameObjects gameObjects;

    public KeyController(DungeonMap map, DungeonScenes scenes, GameObjects gameObjects) {
        this.map = map;
        this.scenes = scenes;
        this.gameObjects = gameObjects;
    }

    @Override
    public boolean keyTyped(char character) {
        switch(character){
            case 'w':   tryMoveRogue(0, 1, Direction.NORTH); return true;
            case 'a':   tryMoveRogue(1, 0, Direction.EAST); return true;
            case 's':   tryMoveRogue(0, -1, Direction.SOUTH); return true;
            case 'd':   tryMoveRogue(-1,0, Direction.WEST); return true;
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
        scenes.turnRogue(map, dir, x, y);
        x += dx;
        y += dy;
        TileType cell = map.getGrid(x, y);
        if(walkable(cell)) {
            GameObject occupant = gameObjects.getOccupant(x, y);
            if(occupant != null){
                Gdx.app.log("occupant", occupant.type.name);
                if(occupant.type.pickup){
                    Gdx.app.log("Pickup", occupant.type.name);
                                                            // assumes gold
                    MessageBox.addLine("You picked up "+occupant.goldQuantity+" "+occupant.type.name);
                    gameObjects.clearOccupant(x, y);
                    scenes.remove(occupant.scene);
                    if(occupant.type == GameObjectTypes.gold){
                        scenes.getRogue().goldQuantity += occupant.goldQuantity;
                    }
                }
            }
            scenes.moveRogue(map, gameObjects, x, y);
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
        GameObject gold = scenes.placeObject(gameObjects, GameObjectTypes.gold, rogue.x, rogue.y);
        gold.goldQuantity = 1;
    }

    private boolean walkable(TileType cell){

        return (cell == TileType.ROOM || cell == TileType.CORRIDOR || cell == TileType.DOORWAY);
    }

    private void equip( int equipped ){
        scenes.getRogue().equipped = equipped;
        scenes.adaptModel(scenes.getRogue().scene, equipped);
    }
}
