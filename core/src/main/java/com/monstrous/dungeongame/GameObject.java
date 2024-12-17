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
    public int quantity;                // e.g. amount of gold for a gold object
    public GameObject attackedBy;       // normally null
    public int protection;              // for armour
    public int damage;                  // for weapons
    public int accuracy;                // for weapons



    public GameObject(GameObjectType type, int x, int y, Direction direction) {
        this.type = type;
        this.x = x;
        this.y = y;
        this.direction = direction;
        this.quantity = 1;
        this.attackedBy = null;
        this.protection = 0;
        this.damage = 0;
        this.accuracy = 0;
    }

    // NPC step
    public void step(World world, DungeonScenes scenes){
        if(attackedBy != null) {     // don't move while being attacked
            defend(world, scenes);
            return;
        }

        // warrior switches aggression on and off
        if(type == GameObjectTypes.warrior){
            if(MathUtils.random(20) < 1)
                stats.aggressive = !stats.aggressive;
        }


        if(stats.aggressive && MathUtils.random(2) > 1){
            // move towards the player

            int dx = (int) Math.signum(world.rogue.x - x);
            int dy = (int) Math.signum(world.rogue.y - y);
            if(dx != 0 && dy != 0){ // avoid diagonals
                if(MathUtils.random(1)>0)
                    dx = 0;
                else
                    dy = 0;
            }
            Direction dir = Direction.NORTH;
            if(dx < 0)
                dir = Direction.WEST;
            else if (dx > 0)
                dir = Direction.EAST;
            else if (dy < 0)
                dir = Direction.SOUTH;
            tryMove(world, scenes, dx, dy, dir);
        }
        else {
            int action = MathUtils.random(0, 3);
            switch (action) {
                // left/right keys translate to -x/+x
                // up/down to +y/-y
                //
                case 0:
                    tryMove(world, scenes, 0, 1, Direction.NORTH);
                    break;
                case 1:
                    tryMove(world, scenes, -1, 0, Direction.WEST);
                    break;
                case 2:
                    tryMove(world, scenes, 0, -1, Direction.SOUTH);
                    break;
                case 3:
                    tryMove(world, scenes, 1, 0, Direction.EAST);
                    break;
            }
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
        if(!TileType.walkable(cell, from)){
            if(scene != null) {
                scene.animationController.setAnimation(null);   // remove previous animation
                scene.animationController.setAnimation("Idle", 1);
            }
            return;     // don't move to non walkable cell
        }


        // what is in the target cell? can be enemy, pickup or nothing
        GameObject occupant  = world.levelData.gameObjects.getOccupant(tx, ty);

        if( occupant != null && occupant.type == GameObjectTypes.bigSword && !type.isPlayer ) {  // don't let monsters pick up sword or walk over sword
            return;
        }


//        if(type.isPlayer)
//            System.out.println("Occupant: "+(occupant == null? "--" : occupant.type.name));

        GameObject opponent = null;
        if(occupant != null && occupant.type.isEnemy){
            opponent = occupant;
        }
        if(!type.isPlayer && tx == world.rogue.x && ty == world.rogue.y){
            opponent = world.rogue;
        }
        if(opponent != null){
            if(type == GameObjectTypes.imp && MathUtils.random(6) > 3)
                rob(world,  opponent);
            else
                fight(world, scenes, opponent);
            return;
        }

        // vacate old tile
        if(!type.isPlayer) {
            world.levelData.gameObjects.clearOccupant(x, y);
        }
        // move to new tile
        x = tx;
        y = ty;
        switch( world.map.getGrid(x,y)){
            case STAIRS_DOWN:           z = -2; break;
            case STAIRS_DOWN_DEEP:      z = -6; break;
            case STAIRS_UP:             z = 2; break;
            case STAIRS_UP_HIGH:        z = 6; break;
            default:                    z = 0; break;
        }
        scenes.moveObject( this, x, y, z);

        boolean pickingUpAnimation = false;
        if(occupant != null && occupant.type.pickup) {
            pickUp(world, scenes, occupant);
            pickingUpAnimation = true;

        }
        if(!type.isPlayer) {
            world.levelData.gameObjects.setOccupant(x, y, this);

            // if enemy goes into fog of war, hide it
            if(scene != null && !world.levelData.tileSeen[y][x]){
                scenes.removeScene( this );
            }
            // and vice versa
            if(scene == null && world.levelData.tileSeen[y][x]){
                scenes.addScene( this );
            }
        }
        if(scene != null && !pickingUpAnimation) {
            scene.animationController.setAnimation(null);   // remove previous animation
            scene.animationController.setAnimation("Walking_A", 1);
        }

    }

    // this character will steal all victim's gold
    private void rob(World world, GameObject victim ){
        if(victim.stats.gold > 0){
            int amount = victim.stats.inventory.removeGold();
            stats.gold += amount;
            GameObject gold = new GameObject(GameObjectTypes.gold, 0, 0, Direction.NORTH);
            stats.inventory.addItem(gold);
            if(type.isPlayer || victim.type.isPlayer || world.rogue.stats.increasedAwareness > 0) {
                Sounds.pickup();
                MessageBox.addLine(type.name + " stole " + amount + " gold from " + victim.type.name);
            }
        }
    }


    private void pickUp(World world, DungeonScenes scenes, GameObject item ){
        //Gdx.app.log("Pickup", item.type.name);

        if(scene != null) {
            scene.animationController.setAnimation(null);   // remove previous animation
            scene.animationController.setAnimation("PickUp", 1);
        }

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
            world.levelData.gameObjects.clearOccupant(x, y);
            world.levelData.gameObjects.remove(item);
            if (item.type == GameObjectTypes.gold) {
                stats.gold += item.quantity;
            }
            if(!type.isPlayer)
                autoEquip(scenes, item);
            if (item.type == GameObjectTypes.bigSword) {
                MessageBox.addLine("This is what you came for!");
                MessageBox.addLine("Now return it to the surface.");
                if(scene!= null) {
                    scene.animationController.setAnimation(null);   // remove previous animation
                    scene.animationController.setAnimation("Cheer", 3);
                }
            }
        }
    }

    // used by enemies to equip weapons or armour
    private void autoEquip( DungeonScenes scenes, GameObject item ){

        if(item.type.isArmour){
            GameObject prev = stats.armourItem;
            // if it better that currently equipped one? or nothing equipped yet?
            if(prev == null || item.protection > prev.protection) {
                stats.armourItem = item;
                // remove item from inventory
                stats.inventory.removeItem(item);
                // put old one back in inventory
                if (prev != null) {
                    if(scene !=null)
                        scenes.detachModel(scene, "handslot.l", prev);
                    stats.inventory.addItem(prev);
                }
                if(scene!=null)
                    scenes.attachModel(scene, "handslot.l",  item);
            }
        } else if(item.type.isWeapon){
            GameObject prev = stats.weaponItem;
            if(prev == null || item.damage > prev.damage) {
                stats.weaponItem = item;
                stats.inventory.removeItem(item);
                if (prev != null) {
                    if(scene !=null)
                        scenes.detachModel(scene, "handslot.r", prev);
                    stats.inventory.addItem(prev);
                }
                if(scene !=null)
                    scenes.attachModel(scene, "handslot.r",  item);
            }
        }
    }

    private void fight(World world, DungeonScenes scenes, GameObject other) {


        if (type.isPlayer || other.type.isPlayer) {
            GameObject enemy = this;
            if (type.isPlayer)
                enemy = other;

            System.out.println("enemy "+enemy.type.name+ " xp:" + enemy.stats.experience + " hp:" + enemy.stats.hitPoints + " wp:" + (enemy.stats.weaponItem==null?"none":enemy.stats.weaponItem.type.name) +
                " armour:" + (enemy.stats.armourItem == null?"none":enemy.stats.armourItem.type.name));
        }

        if(scene != null){
            scene.animationController.setAnimation(null);   // remove previous animation
            scene.animationController.setAnimation("Unarmed_Melee_Attack_Punch_A", 1);
        }
        int hp = 1;
        String verb = "hits";
        if(stats.weaponItem != null) {
            hp += stats.weaponItem.damage;
            verb = "attacks";
        }
        hp += stats.experience /10;     // to tune
        int accuracy = stats.experience/5;
        if(stats.weaponItem != null)
            accuracy += stats.weaponItem.accuracy;
        if(MathUtils.random(5+accuracy) < 3 ){
            if(type.isPlayer || other.type.isPlayer ) {
                Sounds.swoosh();

            }
            MessageBox.addLine(type.name + " misses.");
        }
        else {
            if(type.isPlayer || other.type.isPlayer )
                Sounds.fight();
            if (other.stats.armourItem != null && other.stats.armourItem.protection > hp) {
                MessageBox.addLine("The " + other.type.name + " blocks the attack");
                if (hp > 3) {
                    other.stats.armourItem.protection--;        // armour takes damage
                    MessageBox.addLine("The armour takes damage.");
                }
                if(stats.weaponItem != null){
                    MessageBox.addLine("The weapon takes damage.");
                    stats.weaponItem.accuracy = Math.max(0, stats.weaponItem.accuracy-1);
                }
            } else {
                other.stats.hitPoints = Math.max(0, other.stats.hitPoints - hp);
                // with increased awareness player is informed of all events
                if (type.isPlayer || other.type.isPlayer || world.rogue.stats.increasedAwareness > 0) {
                    MessageBox.addLine(type.name + " " + verb + " the " + other.type.name + "(HP: " + other.stats.hitPoints + ")");
                }
            }
        }
        if(other.stats.hitPoints <= 0){
            defeat(world, scenes, other);
        }
    }

    // something was thrown at the target
    public void hits(World world, DungeonScenes scenes, GameObject thrower, GameObject target){
        Sounds.fight();

        int hp = 1;

        if(type == GameObjectTypes.knife)
            hp = 3;
        else if(type == GameObjectTypes.explosive) {
            hp = 10;
        }
        else if(type == GameObjectTypes.arrow) {
            // if crossbow is equipped arrows do more damage
            if(thrower.stats.weaponItem != null && thrower.stats.weaponItem.type == GameObjectTypes.crossbow)
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
            MessageBox.addLine(type.name + " hits the " + target.type.name + "(HP: " + target.stats.hitPoints + ")");
        }
        if(target.stats.hitPoints <= 0){
            thrower.defeat(world, scenes, target);
        }
    }


    private void defeat(World world, DungeonScenes scenes, GameObject enemy){
        if(type.isPlayer || enemy.type.isPlayer )
            Sounds.monsterDeath();
        MessageBox.addLine(type.name+ " defeated the "+enemy.type.name+". (XP +"+enemy.stats.experience+")");
        if(!enemy.type.isPlayer)
            scenes.remove(enemy.scene);
        world.levelData.gameObjects.clearOccupant(enemy.x, enemy.y);
        world.enemies.remove(enemy);
        world.levelData.gameObjects.remove(enemy);
        stats.experience += enemy.stats.experience;
        if(enemy.stats.gold > 0) {
            GameObject gold = new GameObject(GameObjectTypes.gold, 0,0,Direction.NORTH);
            gold.quantity = enemy.stats.gold;
            enemy.stats.inventory.removeGold();
            scenes.placeObject(world.levelData.gameObjects, GameObjectTypes.gold, enemy.x, enemy.y);
            MessageBox.addLine(enemy.type.name+ " drops their gold. (+"+enemy.stats.gold+")");
            enemy.stats.gold = 0;
        }
    }

}
