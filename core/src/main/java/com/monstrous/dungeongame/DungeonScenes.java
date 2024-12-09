package com.monstrous.dungeongame;

// class to add Scenes to SceneManager to reflect the dungeon rooms

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.graphics.g3d.model.NodePart;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
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

    private SceneManager sceneManager;

    private SceneAsset sceneAssetFloor;
    private SceneAsset sceneAssetWall;
    private SceneAsset sceneAssetDoorWay;
    private SceneAsset sceneAssetCorner;


    public DungeonScenes(SceneManager sceneManager) {
        this.sceneManager = sceneManager;

        sceneAssetFloor = new GLTFLoader().load(Gdx.files.internal("models/floor_tile_large.gltf"));
        sceneAssetWall = new GLTFLoader().load(Gdx.files.internal("models/wall.gltf"));
        sceneAssetDoorWay = new GLTFLoader().load(Gdx.files.internal("models/wall_open_scaffold.gltf"));
        sceneAssetCorner = new GLTFLoader().load(Gdx.files.internal("models/wall_corner.gltf"));
    }

    public void buildMap(DungeonMap map){

        for(int x = 0; x < map.mapWidth; x++){
            for(int y = 0; y < map.mapHeight; y++){
                Scene tile;
                TileType cell = map.getGrid(x,y);
                if(cell != TileType.VOID){
                    tile = new Scene(sceneAssetFloor.scene);
                    setTransform(tile.modelInstance.transform, x, y, Direction.NORTH);
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
                    setTransform(tile.modelInstance.transform, x, y, map.tileOrientation[y][x]);
                    sceneManager.addScene(tile);
                }
            }
        }

    }

    public void populateMap(World world){

        for(int x = 0; x < world.map.mapWidth; x++){
            for(int y = 0; y < world.map.mapHeight; y++){
                GameObject occupant = world.gameObjects.getOccupant(x,y);
                if(occupant != null && occupant.type == GameObjectTypes.gold){
                    addScene(occupant);
                }
            }
        }
    }

    public GameObject placeObject(GameObjects gameObjects, GameObjectType type, int x, int y){
        GameObject go = new GameObject(type, x, y, Direction.SOUTH);

        addScene(go);
        gameObjects.add(go);
        if(!type.isPlayer)
            gameObjects.setOccupant(x, y, go);
        // how to handle enemies walking over gold etc.
        return go;
    }

    public void addScene(GameObject gameObject){

        Scene item = new Scene(gameObject.type.sceneAsset.scene);
        setTransform(item.modelInstance.transform, gameObject.x, gameObject.y, Direction.SOUTH);
        sceneManager.addScene(item);
        gameObject.scene = item;
    }

    public void placeRogue(World world){

        for(int x = 0; x < world.map.mapWidth; x++){
            for(int y = 0; y < world.map.mapHeight; y++){
                GameObject occupant = world.gameObjects.getOccupant(x,y);
                if(occupant != null && occupant.type == GameObjectTypes.rogue){
                    rogue = occupant;
                    addScene(rogue);
                    adaptModel(rogue.scene, Equipped.NONE);
                    return;
                }
            }
        }
    }

    // The next two methods should be the only place where we convert logical x,y to a transform
    //
    private void setTransform(Matrix4 transform, int x, int y, Direction dir){
        transform.setToRotation(Vector3.Y, 180-dir.ordinal() * 90);
        transform.setTranslation(SCALE*x, 0, -SCALE*y);

    }

    // leave orientation as it is
    private void setTransform(Matrix4 transform, int x, int y){
        transform.setTranslation(SCALE*x, 0, -SCALE*y);
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
        setTransform(rogue.scene.modelInstance.transform, x, y, dir);
    }

    public void moveRogue( int x, int y){
        rogue.x = x;
        rogue.y = y;
        setTransform(rogue.scene.modelInstance.transform, x, y);

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
    }
}
