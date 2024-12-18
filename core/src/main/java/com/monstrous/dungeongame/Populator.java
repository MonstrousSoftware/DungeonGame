package com.monstrous.dungeongame;

import com.badlogic.gdx.math.MathUtils;

public class Populator {


    public static void distributeGoodies(DungeonMap map, GameObjects gameObjects){

        int numRooms = map.rooms.size;
        int count = MathUtils.random(numRooms/2, numRooms*3);        // nr of drops depends on nr of rooms
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

                int goodieType = MathUtils.random(0,1);
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
                    case 20:
                    case 21:
                        type = GameObjectTypes.arrows;
                        break;
                    case 22:
                        type = GameObjectTypes.axe;
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
                occupant.quantity = MathUtils.random(1,30);
            else if(type == GameObjectTypes.arrows)
                occupant.quantity = MathUtils.random(3,8);
            else if(type.isArmour)
                occupant.protection = type.initProtection + MathUtils.random(-2, 2);
            else if(type.isWeapon) {
                occupant.damage = type.initDamage + MathUtils.random(-1, 3);
                occupant.accuracy = type.initAccuracy + MathUtils.random(-1, 3);
            }

            count--;
            if(count == 0)
                return;
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
            sword.z = sword.type.z;
            sword.quantity = 1;
            sword.direction = Direction.SOUTH;
            sword.damage = sword.type.initDamage;
            sword.accuracy = sword.type.initAccuracy;
            return;
        }
    }

    public static void distributeEnemies(DungeonMap map, int level, GameObjects gameObjects, Enemies enemies ){

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

            int enemyType = MathUtils.random(3, 3);
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
            assert type != null;
            enemy.stats.experience = type.initXP * (1+MathUtils.random(level*10));     // at lower levels, enemies get more experienced
            int goldAmount = MathUtils.random(0,5);
            if(goldAmount > 0){
                GameObject gold = new GameObject(GameObjectTypes.gold, goldAmount);
                enemy.stats.inventory.addItem(gold);
            }
            enemy.stats.aggressive = enemy.type.initAggressive;
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


}
