package com.monstrous.dungeongame;

// class to add Scenes to SceneManager to reflect the dungeon rooms

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import net.mgsx.gltf.loaders.glb.GLBLoader;
import net.mgsx.gltf.loaders.gltf.GLTFLoader;
import net.mgsx.gltf.scene3d.scene.Scene;
import net.mgsx.gltf.scene3d.scene.SceneAsset;
import net.mgsx.gltf.scene3d.scene.SceneManager;

import static com.monstrous.dungeongame.DungeonMap.*;

public class DungeonScenes implements Disposable {
    private final static float SCALE = 4f;

    private Scene Rogue;
    public int rogueX, rogueY;

    private SceneManager sceneManager;

    private SceneAsset sceneAssetFloor;
    private SceneAsset sceneAssetWall;
    private SceneAsset sceneAssetDoorWay;
    private SceneAsset sceneAssetCorner;
    private SceneAsset sceneAssetGold;
    private SceneAsset sceneAssetRogue;

    public DungeonScenes(SceneManager sceneManager) {
        this.sceneManager = sceneManager;

        sceneAssetFloor = new GLTFLoader().load(Gdx.files.internal("models/floor_tile_large.gltf"));
        sceneAssetWall = new GLTFLoader().load(Gdx.files.internal("models/wall.gltf"));
        sceneAssetDoorWay = new GLTFLoader().load(Gdx.files.internal("models/wall_open_scaffold.gltf"));
        sceneAssetCorner = new GLTFLoader().load(Gdx.files.internal("models/wall_corner.gltf"));
        sceneAssetGold = new GLTFLoader().load(Gdx.files.internal("models/coin_stack_small.gltf"));

        sceneAssetRogue = new GLBLoader().load(Gdx.files.internal("characters/Rogue.glb"));
    }

    public void buildMap(DungeonMap map){

        for(int x = 0; x < map.mapWidth; x++){
            for(int y = 0; y < map.mapHeight; y++){
                Scene tile;
                int cell = map.getGrid(x,y);
                if(cell != 0){
                    tile = new Scene(sceneAssetFloor.scene);
                    tile.modelInstance.transform.setTranslation(SCALE*x, 0, SCALE*y);
                    sceneManager.addScene(tile);
                }
                tile = null;
                if(cell == WALL){
                    tile = new Scene(sceneAssetWall.scene);

                }
                else if(cell == DOORWAY){
                    tile = new Scene(sceneAssetDoorWay.scene);
                }
                else if(cell == CORNER){
                    tile = new Scene(sceneAssetCorner.scene);
                }

                if(tile != null) {
                    tile.modelInstance.transform.setTranslation(SCALE * x, 0, SCALE * y);
                    if(map.orientation[y][x] != Direction.NORTH)
                        tile.modelInstance.transform.rotate(Vector3.Y, map.orientation[y][x].ordinal() * 90);
                    sceneManager.addScene(tile);
                }
            }
        }

    }

    public void populateMap(DungeonMap map){

        for(int x = 0; x < map.mapWidth; x++){
            for(int y = 0; y < map.mapHeight; y++){
                Scene item;
                if(map.occupance[y][x] == GOLD){
                    item = new Scene(sceneAssetGold.scene);
                    item.modelInstance.transform.setTranslation(SCALE*x, 0, SCALE*y);
                    sceneManager.addScene(item);
                }
            }
        }
    }

    public void placeRogue(DungeonMap map){

        for(int x = 0; x < map.mapWidth; x++){
            for(int y = 0; y < map.mapHeight; y++){
                if(map.occupance[y][x] == ROGUE){
                    rogueX = x;
                    rogueY = y;
                    Rogue = new Scene(sceneAssetRogue.scene);
                    Rogue.modelInstance.transform.setTranslation(SCALE*x, 0, SCALE*y);
                    Rogue.modelInstance.transform.rotate(Vector3.Y, map.orientation[y][x].ordinal() * 90);
                    sceneManager.addScene(Rogue);
                    return;
                }
            }
        }
    }

    public void turnRogue(DungeonMap map, Direction dir, int x, int y ){
        map.orientation[y][x] = dir;
        Rogue.modelInstance.transform.setToRotation(Vector3.Y, map.orientation[y][x].ordinal() * 90);
        Rogue.modelInstance.transform.setTranslation(SCALE*x, 0, SCALE*y);
    }

    public void moveRogue(DungeonMap map, int x, int y){
        map.occupance[rogueY][rogueX] = EMPTY;
        map.occupance[y][x] = ROGUE;
        rogueX = x;
        rogueY = y;
        Rogue.modelInstance.transform.setTranslation(SCALE*x, 0, SCALE*y);
    }

    public Scene getRogue(){
        return Rogue;
    }


    @Override
    public void dispose() {
        sceneAssetFloor.dispose();
        sceneAssetWall.dispose();
        sceneAssetDoorWay.dispose();
        sceneAssetCorner.dispose();
        sceneAssetGold.dispose();
        sceneAssetRogue.dispose();
    }
}
