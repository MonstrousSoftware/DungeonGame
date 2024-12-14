package com.monstrous.dungeongame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.MathUtils;
import net.mgsx.gltf.scene3d.scene.Scene;

public class GameObject {

    public GameObjectType type;
    public int x,y;
    public float z;       // above/below ground level, e.g. when walking stairs
    public Direction direction;
    public Scene scene;
    public CharacterStats stats;        // only for rogue and enemies
    public int quantity;            // e.g. amount of gold for a gold object
    public GameObject attackedBy;       // normally null



    public GameObject(GameObjectType type, int x, int y, Direction direction) {
        this.type = type;
        this.x = x;
        this.y = y;
        this.direction = direction;
        this.quantity = 0;
        this.attackedBy = null;
    }

    // NPC step
    public void step(World world, DungeonScenes scenes){
        if(attackedBy != null) {     // don't move while being attacked
            defend(world, scenes);
            return;
        }

        int action = MathUtils.random(0,3);
        switch(action){
            // left/right keys translate to -x/+x
            // up/down to +y/-y
            //
            case 0:   tryMove( world, scenes, 0, 1, Direction.NORTH); break;
            case 1:   tryMove(world, scenes, -1, 0, Direction.WEST); break;
            case 2:   tryMove(world, scenes, 0, -1, Direction.SOUTH); break;
            case 3:   tryMove(world, scenes, 1,0, Direction.EAST); break;
        }
    }

    public void defend(World world, DungeonScenes scenes){
        fight( world, scenes, attackedBy);
        attackedBy = null;
    }

    public void tryMove(World world, DungeonScenes scenes, int dx, int dy, Direction dir){

        scenes.turnObject(this, dir, x, y);    // turn towards moving direction
        int tx = x+dx;
        int ty = y+dy;
        TileType from = world.map.getGrid(x, y);
        TileType cell = world.map.getGrid(tx, ty);
        if(!TileType.walkable(cell, from))
            return;     // don't move to non walkable cell

        // what is in the target cell? can be enemy, pickup or nothing
        GameObject occupant  = world.gameObjects.getOccupant(tx, ty);
        if(occupant != null && occupant.type.isEnemy){
            fight(world, scenes, occupant);
            return;
        }
        if(!type.isPlayer && tx == world.rogue.x && ty == world.rogue.y){
            //Gdx.app.log("enemy fights rogue", "");
            fight(world, scenes, world.rogue);
            return;
        }

        if(!type.isPlayer) {
            world.gameObjects.clearOccupant(x, y);
        }
        x = tx;
        y = ty;
        scenes.moveObject( this, x, y, z);

        if(occupant != null && occupant.type.pickup) {
            Gdx.app.log("Pickup", occupant.type.name);

            if(stats.inventory.addItem(occupant)){
                Sounds.pickup();

                //String name = type.name;
                if(type.isPlayer) {
                    String name = "You";
                    if (occupant.type.isCountable)
                        MessageBox.addLine(name + " picked up " + occupant.quantity + " " + occupant.type.name);
                    else
                        MessageBox.addLine(name + " picked up a " + occupant.type.name);
                }
                if (occupant.scene != null)
                    scenes.remove(occupant.scene);
                world.gameObjects.clearOccupant(x, y);
                if (occupant.type == GameObjectTypes.gold) {
                    stats.gold += occupant.quantity;
                }
            }
        }
        if(!type.isPlayer) {
            world.gameObjects.setOccupant(x, y, this);
        }
    }


    private void fight(World world, DungeonScenes scenes, GameObject other){
        if(type.isPlayer || other.type.isPlayer )
            Sounds.fight();
        int hp = 1;
        String verb = "hits";
        if(stats.weaponItem != null && stats.weaponItem.type == GameObjectTypes.knife) {
            hp = 2;
            verb = "stabs";
        }
        hp += stats.experience /10;     // to tune
        other.stats.hitPoints = Math.max(0, other.stats.hitPoints-hp);
        MessageBox.addLine(type.name+ " " + verb + " the "+other.type.name+"(HP: "+other.stats.hitPoints+")");
        if(other.stats.hitPoints <= 0){
            defeat(world, scenes, other);
        }
    }


    private void defeat(World world, DungeonScenes scenes, GameObject enemy){
        if(type.isPlayer || enemy.type.isPlayer )
            Sounds.monsterDeath();
        MessageBox.addLine(type.name+ " defeated the "+enemy.type.name+". (XP +"+enemy.stats.experience+")");
        scenes.remove(enemy.scene);
        world.gameObjects.clearOccupant(enemy.x, enemy.y);
        world.enemies.remove(enemy);
        stats.experience += enemy.stats.experience;
        if(enemy.stats.gold > 0) {
            MessageBox.addLine(type.name+ " takes their gold. (+"+enemy.stats.gold+")");
            stats.gold += enemy.stats.gold;
            GameObject gold = new GameObject(GameObjectTypes.gold, 0,0,Direction.NORTH);
            gold.quantity = enemy.stats.gold;
            stats.inventory.addItem( gold );
        }
    }

}
