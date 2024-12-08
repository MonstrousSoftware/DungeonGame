package com.monstrous.dungeongame;

// class to add Scenes to SceneManager to reflect the dungeon rooms

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.graphics.g3d.model.NodePart;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Disposable;
import net.mgsx.gltf.loaders.gltf.GLTFLoader;
import net.mgsx.gltf.scene3d.scene.Scene;
import net.mgsx.gltf.scene3d.scene.SceneAsset;
import net.mgsx.gltf.scene3d.scene.SceneManager;

import static com.monstrous.dungeongame.DungeonMap.*;

public class DungeonScenes implements Disposable {
    private final static float SCALE = 4f;

    public GameObject rogue;
    //private Scene rogueScene;

    private SceneManager sceneManager;

    private SceneAsset sceneAssetFloor;
    private SceneAsset sceneAssetWall;
    private SceneAsset sceneAssetDoorWay;
    private SceneAsset sceneAssetCorner;
//    private SceneAsset sceneAssetGold;
//    private SceneAsset sceneAssetRogue;

    public DungeonScenes(SceneManager sceneManager) {
        this.sceneManager = sceneManager;

        sceneAssetFloor = new GLTFLoader().load(Gdx.files.internal("models/floor_tile_large.gltf"));
        sceneAssetWall = new GLTFLoader().load(Gdx.files.internal("models/wall.gltf"));
        sceneAssetDoorWay = new GLTFLoader().load(Gdx.files.internal("models/wall_open_scaffold.gltf"));
        sceneAssetCorner = new GLTFLoader().load(Gdx.files.internal("models/wall_corner.gltf"));
//        sceneAssetGold = new GLTFLoader().load(Gdx.files.internal("models/coin_stack_small.gltf"));

//        sceneAssetRogue = new GLBLoader().load(Gdx.files.internal("characters/Rogue.glb"));
    }

    public void buildMap(DungeonMap map){

        for(int x = 0; x < map.mapWidth; x++){
            for(int y = 0; y < map.mapHeight; y++){
                Scene tile;
                TileType cell = map.getGrid(x,y);
                if(cell != TileType.VOID){
                    tile = new Scene(sceneAssetFloor.scene);
                    tile.modelInstance.transform.setTranslation(SCALE*x, 0, SCALE*y);
                    sceneManager.addScene(tile);
                }
                tile = null;
                if(cell == TileType.WALL){
                    tile = new Scene(sceneAssetWall.scene);

                }
                else if(cell == TileType.DOORWAY){
                    tile = new Scene(sceneAssetDoorWay.scene);
                }
                else if(cell == TileType.WALL_CORNER){
                    tile = new Scene(sceneAssetCorner.scene);
                }

                if(tile != null) {
                    tile.modelInstance.transform.setTranslation(SCALE * x, 0, SCALE * y);
                    if(map.tileOrientation[y][x] != Direction.NORTH)
                        tile.modelInstance.transform.rotate(Vector3.Y, map.tileOrientation[y][x].ordinal() * 90);
                    sceneManager.addScene(tile);
                }
            }
        }

    }

    public void populateMap(DungeonMap map, GameObjects gameObjects){

        for(int x = 0; x < map.mapWidth; x++){
            for(int y = 0; y < map.mapHeight; y++){
                if(map.initialOccupancy[y][x] == GOLD){
                    GameObject gold = placeObject(gameObjects,GameObjectTypes.gold, x, y );
                    gold.goldQuantity = MathUtils.random(1,20);
                }
            }
        }
    }

    public GameObject placeObject(GameObjects gameObjects, GameObjectType type, int x, int y){
        GameObject go = new GameObject(type, x, y, Direction.SOUTH);
        Scene item = new Scene(type.sceneAsset.scene);
        item.modelInstance.transform.setTranslation(SCALE*x, 0, SCALE*y);
        item.modelInstance.transform.rotate(Vector3.Y, Direction.SOUTH.ordinal() * 90);
        sceneManager.addScene(item);
        go.scene = item;
        gameObjects.add(go);
        if(!type.isPlayer)
            gameObjects.setOccupant(x, y, go);
        // how to handle enemies walking over gold etc.
        return go;
    }

    public void placeRogue(DungeonMap map, GameObjects gameObjects){

        for(int x = 0; x < map.mapWidth; x++){
            for(int y = 0; y < map.mapHeight; y++){
                if(map.initialOccupancy[y][x] == ROGUE){
                    rogue = placeObject(gameObjects, GameObjectTypes.rogue, x, y);
                    adaptModel(rogue.scene, Equipped.NONE);
                    return;
                }
            }
        }
    }

    public void adaptModel(Scene rogue, int equipped){
        ModelInstance instance = rogue.modelInstance;
        for(Node node : instance.nodes){
            checkNode(1, node, equipped);
        }
    }

    // recursive method to enable/disable weapons
    private void checkNode(int level, Node node, int equipped ){
        //Gdx.app.log("Node", "level "+ level + " : "+node.id+ " nodeparts: "+node.parts.size);
        if(node.id.contains("Knife"))
            setNodeParts(node, (equipped & Equipped.KNIFE) != 0);
        else if(node.id.contains("Crossbow"))
            setNodeParts(node, (equipped & Equipped.CROSSBOW) != 0);
        else if(node.id.contains("Throwable"))
            setNodeParts(node, (equipped & Equipped.THROWABLE) != 0);

        for(Node n : node.getChildren()){
            checkNode(level+1, n, equipped);
        }
    }

    private void setNodeParts(Node node, boolean enabled){
        for(NodePart part : node.parts)
            part.enabled = enabled;
    }

    public void turnRogue(DungeonMap map, Direction dir, int x, int y ){
        rogue.direction = dir;
        rogue.scene.modelInstance.transform.setToRotation(Vector3.Y, dir.ordinal() * 90);
        rogue.scene.modelInstance.transform.setTranslation(SCALE*x, 0, SCALE*y);
    }

    public void moveRogue(DungeonMap map, GameObjects gameObjects, int x, int y){
        //gameObjects.clearOccupant(rogue.x, rogue.y);
        rogue.x = x;
        rogue.y = y;
        //gameObjects.setOccupant(rogue.x, rogue.y, rogue);
        rogue.scene.modelInstance.transform.setTranslation(SCALE*x, 0, SCALE*y);
    }

    public void remove(Scene scene){
        sceneManager.removeScene(scene);
    }

    public GameObject getRogue(){
        return rogue;
    }


    @Override
    public void dispose() {
        sceneAssetFloor.dispose();
        sceneAssetWall.dispose();
        sceneAssetDoorWay.dispose();
        sceneAssetCorner.dispose();
//        sceneAssetGold.dispose();
//        sceneAssetRogue.dispose();
    }
}
