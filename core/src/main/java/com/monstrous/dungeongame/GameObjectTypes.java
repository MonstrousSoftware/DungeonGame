package com.monstrous.dungeongame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import net.mgsx.gltf.loaders.glb.GLBLoader;
import net.mgsx.gltf.loaders.gltf.GLTFLoader;

public class GameObjectTypes implements Disposable {
    public static final int ICON_SIZE = 64;

    public static GameObjectType rogue;

    public static GameObjectType warrior;
    public static GameObjectType mage;
    public static GameObjectType minion;
    public static GameObjectType imp;

    public static GameObjectType gold;
    public static GameObjectType knife;
    public static GameObjectType crossbow;
    public static GameObjectType explosive;
    public static GameObjectType shield1;
    public static GameObjectType shield2;

    public static Array<GameObjectType> types;
    public static Sprite emptyIcon;


    public GameObjectTypes() {
        if(rogue != null)
            return;
        types = new Array<>();

        rogue = new GameObjectType("Rogue", true, false);
        rogue.sceneAsset = new GLBLoader().load(Gdx.files.internal("characters/Rogue_Hooded.glb"));
        rogue.isPlayer = true;
        rogue.initXP = 0;
        types.add(rogue);

        warrior = new GameObjectType("Warrior", true, false);
        warrior.sceneAsset = new GLBLoader().load(Gdx.files.internal("characters/Skeleton_Warrior.glb"));
        warrior.isEnemy = true;
        warrior.initXP = 3;
        types.add(warrior);

        mage = new GameObjectType("Mage", true, false);
        mage.sceneAsset = new GLBLoader().load(Gdx.files.internal("characters/Skeleton_Mage.glb"));
        mage.isEnemy = true;
        types.add(mage);

        minion = new GameObjectType("Minion", true, false);
        minion.sceneAsset = new GLBLoader().load(Gdx.files.internal("characters/Skeleton_Minion.glb"));
        minion.isEnemy = true;
        types.add(minion);

        imp = new GameObjectType("Imp", true, false);
        imp.sceneAsset = new GLBLoader().load(Gdx.files.internal("characters/Skeleton_Rogue.glb"));
        imp.isEnemy = true;
        imp.initXP = 2;
        types.add(imp);

        gold = new GameObjectType("Gold", false, true);
        gold.sceneAsset = new GLTFLoader().load(Gdx.files.internal("models/coin_stack_small.gltf"));
        gold.isCountable = true;
        types.add(gold);

        knife = new GameObjectType("Knife", false, true);
        knife.sceneAsset = new GLTFLoader().load(Gdx.files.internal("models/dagger.gltf"));
        knife.isWeapon = true;
        types.add(knife);

        crossbow = new GameObjectType("Crossbow", false, true);
        crossbow.sceneAsset = new GLTFLoader().load(Gdx.files.internal("models/crossbow_1handed.gltf"));
        crossbow.isWeapon = true;
        types.add(crossbow);

        explosive = new GameObjectType("Explosive", false, true);
        explosive.sceneAsset = new GLTFLoader().load(Gdx.files.internal("models/smokebomb.gltf"));
        explosive.isWeapon = true;
        types.add(explosive);

        shield1 = new GameObjectType("Round Shield", false, true);
        shield1.sceneAsset = new GLTFLoader().load(Gdx.files.internal("models/shield_round.gltf"));
        shield1.z = 1f;
        shield1.isArmour = true;
        types.add(shield1);

        shield2 = new GameObjectType("Square Shield", false, true);
        shield2.sceneAsset = new GLTFLoader().load(Gdx.files.internal("models/shield_square.gltf"));
        shield2.z = 1f;
        shield2.isArmour = true;
        types.add(shield2);

        addIcons();
     }

     // generate icons for each type
     private void addIcons(){
        ShowCase showCase = new ShowCase();
        emptyIcon = showCase.makeIcon(null, ICON_SIZE, ICON_SIZE, false);
        for(GameObjectType type : types ){
            Sprite icon = showCase.makeIcon(type.sceneAsset, ICON_SIZE, ICON_SIZE, type.isEnemy || type.isPlayer);
            type.icon = icon;
        }
     }

    @Override
    public void dispose() {
        rogue.sceneAsset.dispose();
        gold.sceneAsset.dispose();
    }

}
