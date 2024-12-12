package com.monstrous.dungeongame;

import com.badlogic.gdx.Gdx;

public class Actor {


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
}
