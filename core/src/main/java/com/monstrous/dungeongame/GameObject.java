package com.monstrous.dungeongame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import net.mgsx.gltf.scene3d.scene.Scene;

public class GameObject {

    public GameObjectType type;
    public int x,y;
    public int z;       // above/below ground level, e.g. when walking stairs
    public Direction direction;
    public Scene scene;
    public CharacterStats stats;
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
        TileType cell = world.map.getGrid(tx, ty);
        if(!TileType.walkable(cell))
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

        world.gameObjects.clearOccupant( x, y);
        world.gameObjects.setOccupant(x, y, this);
        scenes.moveObject( this, x, y, z);

        if(occupant != null && occupant.type.pickup) {
            Gdx.app.log("Pickup", occupant.type.name);
            // assumes gold
            MessageBox.addLine(type.name + " picked up " + occupant.quantity + " " + occupant.type.name);

            if (occupant.scene != null)
                scenes.remove(occupant.scene);
            world.gameObjects.clearOccupant(x, y);
            if (occupant.type == GameObjectTypes.gold) {
                stats.gold += occupant.quantity;
            }

        }
    }


    private void fight(World world, DungeonScenes scenes, GameObject other){
        other.stats.hitPoints -= 1;
        MessageBox.addLine(type.name+ " hit the "+other.type.name+"(HP: "+other.stats.hitPoints+")");
        if(other.stats.hitPoints <= 0){
            MessageBox.addLine(type.name+ " has defeated the "+other.type.name+". (XP +1)");
            if(other.scene != null)
                scenes.remove(other.scene);
            world.gameObjects.clearOccupant(other.x, other.y);
            stats.experience++;
        }
    }
}
