package com.monstrous.dungeongame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import net.mgsx.gltf.loaders.glb.GLBLoader;
import net.mgsx.gltf.loaders.gltf.GLTFLoader;

public class GameObjectTypes implements Disposable {
    public static GameObjectType rogue;
    public static GameObjectType warrior;
    public static GameObjectType mage;
    public static GameObjectType minion;
    public static GameObjectType imp;
    public static GameObjectType gold;
    public static GameObjectType knife;
    public static GameObjectType crossbow;

    public static Array<GameObjectType> types;


    public GameObjectTypes() {
        if(rogue != null)
            return;
        types = new Array<>();

        rogue = new GameObjectType("Rogue", true, false);
        rogue.sceneAsset = new GLBLoader().load(Gdx.files.internal("characters/Rogue_Hooded.glb"));
        rogue.isPlayer = true;
        types.add(rogue);

        warrior = new GameObjectType("Warrior", true, false);
        warrior.sceneAsset = new GLBLoader().load(Gdx.files.internal("characters/Skeleton_Warrior.glb"));
        warrior.isEnemy = true;
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
        types.add(imp);

        gold = new GameObjectType("Gold", false, true);
        gold.sceneAsset = new GLTFLoader().load(Gdx.files.internal("models/coin_stack_small.gltf"));
        types.add(gold);

        knife = new GameObjectType("Knife", false, true);
        types.add(knife);
        crossbow = new GameObjectType("Crossbow", false, true);
        types.add(crossbow);
     }

    @Override
    public void dispose() {
        rogue.sceneAsset.dispose();
        gold.sceneAsset.dispose();
    }

}
