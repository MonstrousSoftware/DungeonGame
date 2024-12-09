package com.monstrous.dungeongame;

import com.badlogic.gdx.math.MathUtils;

public class Populator {


    public static void distributeGold(DungeonMap map, GameObjects gameObjects ){

        int count = MathUtils.random(10, 25);        // nr of gold drops
        while(true){
            int location = MathUtils.random(0, map.rooms.size-1);
            Room room = map.rooms.get(location);
            if(room.isStairWell)
                continue;
            int rx = MathUtils.random(0, room.width-1);
            int ry = MathUtils.random(0, room.height-1);
            GameObject occupant = gameObjects.getOccupant(room.x+rx, room.y+ry);
            if(occupant != null)
                continue;

            occupant = new GameObject(GameObjectTypes.gold, room.x+rx, room.y+ry, Direction.SOUTH);
            gameObjects.setOccupant(room.x+rx, room.y+ry, occupant);
            gameObjects.add(occupant);
            occupant.goldQuantity = MathUtils.random(1,20);
            // seems redundant to provide x,y twice


            count--;
            if(count == 0)
                return;
        }
    }

    public static void placeRogue(DungeonMap map, GameObjects gameObjects){
        while(true) {
            int location = MathUtils.random(0, map.rooms.size-1);
            Room room = map.rooms.get(location);
            if(room.isStairWell)
                continue;
            GameObject occupant = gameObjects.getOccupant(room.centre.x, room.centre.y);
            if(occupant != null)
                continue;

            occupant = new GameObject(GameObjectTypes.rogue, room.centre.x, room.centre.y, Direction.SOUTH);
            gameObjects.setOccupant(room.centre.x, room.centre.y, occupant);
            gameObjects.add(occupant);
            occupant.direction = Direction.SOUTH;

            return;
        }
    }
}
