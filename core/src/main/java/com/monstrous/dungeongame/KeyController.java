package com.monstrous.dungeongame;

import com.badlogic.gdx.InputAdapter;

import static com.monstrous.dungeongame.DungeonMap.*;

public class KeyController extends InputAdapter {

    private DungeonMap map;
    private DungeonScenes scenes;

    public KeyController(DungeonMap map, DungeonScenes scenes) {
        this.map = map;
        this.scenes = scenes;
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

            default:    return false;
        }
    }

    private void tryMoveRogue(int dx, int dy, Direction dir){
        int x = scenes.rogueX;
        int y = scenes.rogueY;
        scenes.turnRogue(map, dir, x, y);
        x += dx;
        y += dy;
        TileType cell = map.getGrid(x, y);
        if(walkable(cell))
            scenes.moveRogue(map, x, y);
    }

    private boolean walkable(TileType cell){

        return (cell == TileType.ROOM || cell == TileType.CORRIDOR || cell == TileType.DOORWAY);
    }

    private void equip( int equipped ){
        // todo store the state for the game logic
        scenes.adaptModel(scenes.getRogue(), equipped);
    }
}
