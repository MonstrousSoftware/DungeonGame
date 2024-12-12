package com.monstrous.dungeongame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;

public class Enemies {
    private Array<GameObject> enemies;
    private World world;
    private DungeonScenes scenes;

    public Enemies(World world) {
        this.world = world;
        //this.scenes = scenes;
        enemies = new Array<>();
    }

    public void add(GameObject enemy){
        enemies.add(enemy);
    }

    public void clear(){
        enemies.clear();
    }

    public void step(DungeonScenes scenes){
        this.scenes = scenes;
        for(GameObject enemy : enemies){
            enemy.step(world, scenes);
        }
    }

//    private void step(GameObject enemy){
//        if(enemy.attackedBy != null) {     // don't move while being attacked
//            defend(enemy);
//            return;
//        }
//
//        int action = MathUtils.random(0,3);
//        switch(action){
//            // left/right keys translate to -x/+x
//            // up/down to +y/-y
//            //
//            case 0:   tryMove(enemy, 0, 1, Direction.NORTH); break;
//            case 1:   tryMove(enemy, -1, 0, Direction.WEST); break;
//            case 2:   tryMove(enemy, 0, -1, Direction.SOUTH); break;
//            case 3:   tryMove(enemy, 1,0, Direction.EAST); break;
//        }
//    }
//
//    private void defend(GameObject enemy){
//        fight(enemy, enemy.attackedBy);
//        enemy.attackedBy = null;
//    }
//
//    private void tryMove(GameObject character, int dx, int dy, Direction dir){
//        int x = character.x;
//        int y = character.y;
//
//
//        scenes.turnObject(character, dir, x, y);    // turn towards moving direction
//        x += dx;
//        y += dy;
//        TileType cell = world.map.getGrid(x, y);
//        if(!TileType.walkable(cell))
//            return;     // don't move to non walkable cell
//
//        GameObject item = world.gameObjects.getOccupant(x, y);
//        if(item != null && item.type.isEnemy){
//            fight(character, item);
//            return;
//        }
//        if(x == world.rogue.x && y == world.rogue.y){
//            //Gdx.app.log("enemy fights rogue", "");
//            fight(character, world.rogue);
//            return;
//        }
//
//        world.gameObjects.clearOccupant( character.x, character.y);
//        world.gameObjects.setOccupant(x, y, character);
//        scenes.moveObject( character, x, y, world.rogue.z);
//
//        if(item != null){
//            Gdx.app.log("occupant", item.type.name);
//            if(item.type.pickup) {
//                Gdx.app.log("Pickup", item.type.name);
//                // assumes gold
//                MessageBox.addLine(character.type.name + " picked up " + item.quantity + " " + item.type.name);
//
//                if (item.scene != null)
//                    scenes.remove(item.scene);
//                world.gameObjects.clearOccupant(x, y);
//                if (item.type == GameObjectTypes.gold) {
//                    character.stats.gold += item.quantity;
//                }
//            }
//        }
//    }
//
//
//    private void fight(GameObject actor, GameObject other){
//        other.stats.hitPoints -= 1;
//        MessageBox.addLine(actor.type.name+ " hit the "+other.type.name+"(HP: "+other.stats.hitPoints+")");
//        if(other.stats.hitPoints <= 0){
//            MessageBox.addLine(actor.type.name+ " has defeated the "+other.type.name+". (XP +1)");
//            if(other.scene != null)
//                scenes.remove(other.scene);
//            world.gameObjects.clearOccupant(other.x, other.y);
//            actor.stats.experience++;
//        }
//    }
}
