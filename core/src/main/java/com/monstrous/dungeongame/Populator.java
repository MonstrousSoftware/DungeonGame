package com.monstrous.dungeongame;

import com.badlogic.gdx.math.MathUtils;

public class Populator {


    public static void distributeGold(DungeonMap map, GameObjects gameObjects ){

        int count = MathUtils.random(10, 25);        // nr of gold drops
        int attempts = 0;
        while(true){
            attempts++;
            if(attempts > 10)       // avoid endless loop
                break;

            int location = MathUtils.random(0, map.rooms.size-1);
            Room room = map.rooms.get(location);
            if(room.isStairWell)
                continue;
            // find a point inside the room (edges are walls)
            int rx = MathUtils.random(1, room.width-3);
            int ry = MathUtils.random(1, room.height-3);
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

    public static void distributeEnemies(DungeonMap map, GameObjects gameObjects ){

        int count = MathUtils.random(2, 10);        //nr of drops
        int attempts = 0;
        while(true){
            attempts++;
            if(attempts > 10)       // avoid endless loop
                break;

            int location = MathUtils.random(0, map.rooms.size-1);
            Room room = map.rooms.get(location);
            if(room.isStairWell)
                continue;
            // find a point inside the room (edges are walls)
            int rx = MathUtils.random(1, room.width-3);
            int ry = MathUtils.random(1, room.height-3);
            GameObject occupant = gameObjects.getOccupant(room.x+rx, room.y+ry);
            if(occupant != null)
                continue;

            int enemyType = MathUtils.random(0, 3);
            GameObjectType type = null;
            switch(enemyType){
                case 0: type = GameObjectTypes.warrior; break;
                case 1: type = GameObjectTypes.mage; break;
                case 2: type = GameObjectTypes.minion; break;
                case 3: type = GameObjectTypes.imp; break;
            }
            occupant = new GameObject(type, room.x+rx, room.y+ry, Direction.SOUTH);
            gameObjects.setOccupant(room.x+rx, room.y+ry, occupant);
            occupant.stats = new CharacterStats();
            gameObjects.add(occupant);
//            occupant.goldQuantity = MathUtils.random(1,20);
            // seems redundant to provide x,y twice


            count--;
            if(count == 0)
                return;
        }
    }

    public static GameObject placeRogue(DungeonMap map, GameObjects gameObjects){
        while(true) {
            // choose random room
            int location = MathUtils.random(0, map.rooms.size-1);
            Room room = map.rooms.get(location);
            if(room.isStairWell)    // not a stairwell
                continue;
            // check if there is something in the centre?
            GameObject occupant = gameObjects.getOccupant(room.centre.x, room.centre.y);
            if(occupant != null)
                continue;

            GameObject rogue = new GameObject(GameObjectTypes.rogue, room.centre.x, room.centre.y, Direction.SOUTH);
            //gameObjects.setOccupant(room.centre.x, room.centre.y, occupant);
            rogue.stats = new CharacterStats();
            gameObjects.add(rogue);
            rogue.direction = Direction.SOUTH;

            return rogue;
        }
    }
}
