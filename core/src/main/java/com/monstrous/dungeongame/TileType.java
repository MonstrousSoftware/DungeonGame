package com.monstrous.dungeongame;

public enum TileType {
    VOID,
    ROOM,
    CORRIDOR,
    WALL,
    DOORWAY,
    STAIRS_DOWN,
    STAIRS_DOWN_DEEP,
    STAIRS_UP,
    STAIRS_UP_HIGH,
    WALL_CORNER,
    WALL_T_SPLIT,
    WALL_CROSSING;


    public static boolean walkable(TileType cell, TileType from){
        switch(cell){
            case ROOM:
            case CORRIDOR:
            case DOORWAY:
            case STAIRS_DOWN:
            case STAIRS_UP:
                return true;
            default:
                break;
        }
        if(cell == STAIRS_UP_HIGH)
            return(from == STAIRS_UP);
        else if(cell == STAIRS_DOWN_DEEP)
            return(from == STAIRS_DOWN);
        return false;
    }
}
