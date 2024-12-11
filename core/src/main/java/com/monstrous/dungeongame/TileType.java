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


    public static boolean walkable(TileType cell){
        switch(cell){
            case ROOM:
            case CORRIDOR:
            case DOORWAY:
            case STAIRS_DOWN:
            case STAIRS_DOWN_DEEP:
            case STAIRS_UP:
            case STAIRS_UP_HIGH:
                return true;
            default:
                return false;
        }
    }
}
