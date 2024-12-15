package com.monstrous.dungeongame;

import com.badlogic.gdx.math.MathUtils;

public class Populator {


    public static void distributeGoodies(DungeonMap map, GameObjects gameObjects){

        int numRooms = map.rooms.size;
        int count = MathUtils.random(numRooms/2, numRooms);        // nr of drops depends on nr of rooms
        int attempts = 0;
        while(true){
            attempts++;
            if(attempts > 40)       // avoid endless loop
                break;

            int location = MathUtils.random(0, map.rooms.size-1);
            Room room = map.rooms.get(location);
            if(room.isStairWell)
                continue;
            // find a point inside the room (edges are walls)
            int rx = MathUtils.random(1, room.width-1);
            int ry = MathUtils.random(1, room.height-1);
            GameObject occupant = gameObjects.getOccupant(room.x+rx, room.y+ry);
            if(occupant != null)
                continue;

            GameObjectType type = null;

                int goodieType = MathUtils.random(0, 19);
                switch (goodieType) {
                    case 0:
                        type = GameObjectTypes.gold;
                        break;
                    case 1:
                        type = GameObjectTypes.knife;
                        break;
                    case 2:
                        type = GameObjectTypes.crossbow;
                        break;
                    case 3:
                        type = GameObjectTypes.explosive;
                        break;
                    case 4:
                        type = GameObjectTypes.shield1;
                        break;
                    case 5:
                        type = GameObjectTypes.shield2;
                        break;
                    case 6:
                        type = GameObjectTypes.spellBookClosed;
                        break;
                    case 7:
                        type = GameObjectTypes.bottle_A_brown;
                        break;
                    case 8:
                        type = GameObjectTypes.bottle_A_green;
                        break;
                    case 9:
                        type = GameObjectTypes.bottle_B_brown;
                        break;
                    case 10:
                        type = GameObjectTypes.bottle_B_green;
                        break;
                    case 11:
                        type = GameObjectTypes.bottle_C_brown;
                        break;
                    case 12:
                        type = GameObjectTypes.bottle_C_green;
                        break;
                    case 13:
                    case 14:
                    case 15:
                    case 16:
                        type = GameObjectTypes.food;
                        break;
                    case 17:
                        type = GameObjectTypes.spellBookClosedB;
                        break;
                    case 18:
                        type = GameObjectTypes.spellBookClosedC;
                        break;
                    case 19:
                        type = GameObjectTypes.spellBookClosedD;
                        break;
                }


            occupant = new GameObject(type, room.x+rx, room.y+ry, Direction.SOUTH);
            gameObjects.setOccupant(room.x+rx, room.y+ry, occupant);
            // seems redundant to provide x,y twice

            gameObjects.add(occupant);
            assert type != null;
            occupant.z = type.z;
            occupant.quantity = 1;
            if(type == GameObjectTypes.gold)
                occupant.quantity = MathUtils.random(1,20);

            count--;
            if(count == 0)
                return;
        }
    }

    public static void distributeEnemies(DungeonMap map, GameObjects gameObjects, Enemies enemies ){

        enemies.clear();
        int numRooms = map.rooms.size;
        int count = MathUtils.random(numRooms/4, numRooms*numRooms/4);        // nr of drops depends on nr of rooms
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
            int rx = MathUtils.random(1, room.width-1);
            int ry = MathUtils.random(1, room.height-1);

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
            GameObject enemy = new GameObject(type, room.x+rx, room.y+ry, Direction.SOUTH);
            gameObjects.setOccupant(room.x+rx, room.y+ry, enemy);
            enemy.stats = new CharacterStats();
            enemy.stats.experience = type.initXP;
            gameObjects.add(enemy);
            enemies.add(enemy);
            // seems redundant to provide x,y twice


            count--;
            if(count == 0)
                return;
        }
    }

    public static GameObject placeRogue(DungeonMap map, GameObjects gameObjects){
        while(true) {
            // choose random room
            int location = MathUtils.random(1, map.rooms.size-1);
            Room room = map.rooms.get(location);
            if(room.isStairWell)    // not a stairwell
                continue;
            // check if there is something in the centre?
            GameObject occupant = gameObjects.getOccupant(room.centre.x, room.centre.y);
            if(occupant != null)
                continue;

            GameObject rogue = new GameObject(GameObjectTypes.rogue, room.centre.x, room.centre.y, Direction.SOUTH);
            gameObjects.setOccupant(room.centre.x, room.centre.y, rogue);
            rogue.stats = new CharacterStats();
            gameObjects.add(rogue);
            rogue.direction = Direction.SOUTH;

            return rogue;
        }
    }

    public static void placeSword(DungeonMap map, GameObjects gameObjects){
        while(true) {
            // choose random room
            int location = MathUtils.random(1, map.rooms.size-1);
            Room room = map.rooms.get(location);
            if(room.isStairWell)    // not a stairwell
                continue;
            // check if there is something in the centre?
            GameObject occupant = gameObjects.getOccupant(room.centre.x, room.centre.y);
            if(occupant != null)
                continue;

            GameObject sword = new GameObject(GameObjectTypes.bigSword, room.centre.x, room.centre.y, Direction.SOUTH);
            gameObjects.setOccupant(room.centre.x, room.centre.y, sword);
            gameObjects.add(sword);
            sword.direction = Direction.SOUTH;
            return;
        }
    }
}
