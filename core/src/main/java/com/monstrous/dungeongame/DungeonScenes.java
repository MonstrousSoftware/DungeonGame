package com.monstrous.dungeongame;

// class to add Scenes to SceneManager to reflect the dungeon rooms

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import net.mgsx.gltf.loaders.gltf.GLTFLoader;
import net.mgsx.gltf.scene3d.scene.Scene;
import net.mgsx.gltf.scene3d.scene.SceneAsset;
import net.mgsx.gltf.scene3d.scene.SceneManager;

import static com.monstrous.dungeongame.DungeonMap.*;

public class DungeonScenes implements Disposable {

    SceneAsset sceneAssetFloor;
    SceneAsset sceneAssetWall;
    SceneAsset sceneAssetDoorWay;
    SceneAsset sceneAssetCorner;

    public DungeonScenes() {
        sceneAssetFloor = new GLTFLoader().load(Gdx.files.internal("models/floor_tile_large.gltf"));
        sceneAssetWall = new GLTFLoader().load(Gdx.files.internal("models/wall.gltf"));
        sceneAssetDoorWay = new GLTFLoader().load(Gdx.files.internal("models/wall_open_scaffold.gltf"));
        sceneAssetCorner = new GLTFLoader().load(Gdx.files.internal("models/wall_corner.gltf"));
    }

    public void buildMap(SceneManager sceneManager, DungeonMap map){

        for(int x = 0; x < map.mapWidth; x++){
            for(int y = 0; y < map.mapHeight; y++){
                Scene tile;
                if(map.grid[y][x] != 0){
                    tile = new Scene(sceneAssetFloor.scene);
                    tile.modelInstance.transform.setTranslation(4*x, 0, 4*y);
                    sceneManager.addScene(tile);
                }
                tile = null;
                if(map.grid[y][x] == WALL){
                    tile = new Scene(sceneAssetWall.scene);

                }
                else if(map.grid[y][x] == DOORWAY){
                    tile = new Scene(sceneAssetDoorWay.scene);
                }
                else if(map.grid[y][x] == CORNER){
                    tile = new Scene(sceneAssetCorner.scene);
                }

                if(tile != null) {
                    tile.modelInstance.transform.setTranslation(4 * x, 0, 4 * y);
                    if(map.orientation[y][x] != 0)
                        tile.modelInstance.transform.rotate(Vector3.Y, map.orientation[y][x] * 90);
                    sceneManager.addScene(tile);
                }
            }
        }

    }

    @Override
    public void dispose() {
        sceneAssetFloor.dispose();
        sceneAssetWall.dispose();
        sceneAssetDoorWay.dispose();
        sceneAssetCorner.dispose();
    }
}
