package com.monstrous.dungeongame;

import com.badlogic.gdx.Gdx;
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
        this.quantity = 1;
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
            pickUp(world, scenes, occupant);
        }
        if(!type.isPlayer) {
            world.gameObjects.setOccupant(x, y, this);

            // if enemy goes into fog of war, hide it
            if(scene != null && !world.map.tileSeen[y][x]){
                scenes.removeScene( this );
            }
            // and vice versa
            if(scene == null && world.map.tileSeen[y][x]){
                scenes.addScene( this );
            }
        }
    }

//    private void steal(World world, DungeonScenes scenes, GameObject character ){
//        if(character.stats.gold > 0){
//            character.stats.inventory.takeAllGold();
//
//        }
//
//    }


    private void pickUp(World world, DungeonScenes scenes, GameObject item ){
        Gdx.app.log("Pickup", item.type.name);

        if(stats.inventory.addItem(item)){  // if there is room in the inventory

            String name = type.name;
            if(type.isPlayer)
                name = "You";

            // with increased awareness player is informed of all events
            // otherwise report only on player actions
            //
            if(type.isPlayer || world.rogue.stats.increasedAwareness > 0) {
                Sounds.pickup();
                if (item.type.isCountable)
                    MessageBox.addLine(name + " picked up " + item.quantity + " " + item.type.name);
                else
                    MessageBox.addLine(name + " picked up " + item.type.name);
            }
            if (item.scene != null)
                scenes.remove(item.scene);
            world.gameObjects.clearOccupant(x, y);
            world.gameObjects.remove(item);
            if (item.type == GameObjectTypes.gold) {
                stats.gold += item.quantity;
            }
            if (item.type == GameObjectTypes.bigSword) {
                MessageBox.addLine("This is what you came for!");
                MessageBox.addLine("Now return it to the surface.");
            }
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
        // with increased awareness player is informed of all events
        if(type.isPlayer || world.rogue.stats.increasedAwareness > 0) {
            MessageBox.addLine(type.name + " " + verb + " the " + other.type.name + "(HP: " + other.stats.hitPoints + ")");
        }
        if(other.stats.hitPoints <= 0){
            defeat(world, scenes, other);
        }
    }

    public void hits(World world, DungeonScenes scenes, GameObject thrower, GameObject target){
        Sounds.fight();

        int hp = 1;

        if(type == GameObjectTypes.knife)
            hp = 3;
        else if(type == GameObjectTypes.explosive) {
            hp = 10;
        }
        else if(type == GameObjectTypes.arrows) {
            // if crossbow is equipped arrows do more damage
            if(thrower.stats.weaponItem.type == GameObjectTypes.crossbow)
                hp = 10;
            else
                hp = 3;
        }
        else if(type == GameObjectTypes.bottle_C_green){
            hp = 3;     // poison
        } else if(type == GameObjectTypes.bottle_B_green) {
            hp = -3;        // invigorating
        }

        target.stats.hitPoints = Math.max(0, target.stats.hitPoints-hp);
        // with increased awareness player is informed of all events
        if(target.type.isPlayer || thrower.type.isPlayer || world.rogue.stats.increasedAwareness > 0) {
            MessageBox.addLine(type.name + " hits  the " + target.type.name + "(HP: " + target.stats.hitPoints + ")");
        }
        if(target.stats.hitPoints <= 0){
            thrower.defeat(world, scenes, target);
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
            GameObject gold = new GameObject(GameObjectTypes.gold, 0,0,Direction.NORTH);
            gold.quantity = enemy.stats.gold;
            scenes.placeObject(world.gameObjects, GameObjectTypes.gold, enemy.x, enemy.y);

            MessageBox.addLine(enemy.type.name+ " drops their gold. (+"+enemy.stats.gold+")");
        }
    }

}
