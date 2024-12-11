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

    //public GameObject rogue;

    private SceneManager sceneManager;

    private SceneAsset sceneAssetFloor;
    private SceneAsset sceneAssetWall;
    private SceneAsset sceneAssetWall2;
    private SceneAsset sceneAssetWall3;
    private SceneAsset sceneAssetWall4;
    private SceneAsset sceneAssetWall5;
    private SceneAsset sceneAssetDoorWay;
    private SceneAsset sceneAssetCorner;
    private SceneAsset sceneAssetWallTsplit;
    private SceneAsset sceneAssetWallCrossing;
    private SceneAsset sceneAssetStairs;


    public DungeonScenes(SceneManager sceneManager) {
        this.sceneManager = sceneManager;

        sceneAssetFloor = new GLTFLoader().load(Gdx.files.internal("models/floor_tile_large.gltf"));
        sceneAssetWall = new GLTFLoader().load(Gdx.files.internal("models/wall.gltf"));
        sceneAssetWall2 = new GLTFLoader().load(Gdx.files.internal("models/wall_arched.gltf"));
        sceneAssetWall3 = new GLTFLoader().load(Gdx.files.internal("models/wall_archedwindow_gated.gltf"));
        sceneAssetWall4 = new GLTFLoader().load(Gdx.files.internal("models/wall_archedwindow_gated_scaffold.gltf"));
        sceneAssetWall5 = new GLTFLoader().load(Gdx.files.internal("models/wall_gated.gltf"));
        sceneAssetDoorWay = new GLTFLoader().load(Gdx.files.internal("models/wall_open_scaffold.gltf"));
        sceneAssetCorner = new GLTFLoader().load(Gdx.files.internal("models/wall_corner.gltf"));
        sceneAssetWallTsplit = new GLTFLoader().load(Gdx.files.internal("models/wall_Tsplit.gltf"));
        sceneAssetWallCrossing = new GLTFLoader().load(Gdx.files.internal("models/wall_crossing.gltf"));
        sceneAssetStairs = new GLTFLoader().load(Gdx.files.internal("models/stairs.gltf"));
    }

    public void buildMap(DungeonMap map){
        for(Room room: map.rooms)
            if(room.uncovered)
                buildRoom(map, room);

    }

    public void buildRoom(DungeonMap map, Room room){
        room.uncovered = true;

        for(int x = room.x; x <= room.x + room.width; x++){
            for(int y = room.y; y <= room.y + room.height; y++){
                Scene tile;
                TileType cell = map.getGrid(x,y);
                if(cell!= TileType.VOID && cell != TileType.STAIRS_DOWN && cell != TileType.STAIRS_DOWN_DEEP){
                    tile = new Scene(sceneAssetFloor.scene);
                    setTransform(tile.modelInstance.transform, x, y, 0, Direction.NORTH);
                    sceneManager.addScene(tile);
                }
                tile = null;
                int z = 0;
                if(cell == TileType.WALL){
                    if(MathUtils.random(1.0f) < 0.1f)
                        tile = new Scene(sceneAssetWall2.scene);
                    else if(MathUtils.random(1.0f) < 0.1f)
                        tile = new Scene(sceneAssetWall3.scene);
                    else if(MathUtils.random(1.0f) < 0.1f)
                        tile = new Scene(sceneAssetWall4.scene);
                    else if(MathUtils.random(1.0f) < 0.1f)
                        tile = new Scene(sceneAssetWall5.scene);
                    else
                        tile = new Scene(sceneAssetWall.scene);
                }
                else if(cell == TileType.DOORWAY){
                    tile = new Scene(sceneAssetDoorWay.scene);
                }
                else if(cell == TileType.WALL_CORNER){
                    tile = new Scene(sceneAssetCorner.scene);
                }
                else if(cell == TileType.WALL_T_SPLIT){
                    tile = new Scene(sceneAssetWallTsplit.scene);
                }
                else if(cell == TileType.WALL_CROSSING){
                    tile = new Scene(sceneAssetWallCrossing.scene);
                }
                else if(cell == TileType.STAIRS_DOWN){
                    tile = new Scene(sceneAssetStairs.scene);
                    z = -4;
                }
                else if(cell == TileType.STAIRS_DOWN_DEEP){
                    tile = new Scene(sceneAssetStairs.scene);
                    z = -8;
                }
                else if(cell == TileType.STAIRS_UP){
                    tile = new Scene(sceneAssetStairs.scene);
                    z = 0;
                }
                else if(cell == TileType.STAIRS_UP_HIGH){
                    tile = new Scene(sceneAssetStairs.scene);
                    z = 4;
                }

                if(tile != null) {
                    setTransform(tile.modelInstance.transform, x, y, z, map.tileOrientation[y][x]);
                    sceneManager.addScene(tile);
                }
            }
        }
    }



    // show corridor segment if not seen before
    public void visitCorridorSegment(DungeonMap map, int x, int y){
        if(map.corridorSeen[y][x])
            return;
        map.corridorSeen[y][x] = true;

        TileType cell = map.getGrid(x,y);
        if(cell != TileType.VOID){
            Scene tile = new Scene(sceneAssetFloor.scene);
            setTransform(tile.modelInstance.transform, x, y, 0, Direction.NORTH);
            sceneManager.addScene(tile);
        }
    }

    public void buildCorridors(DungeonMap map){
        for(int x = 0; x < map.mapWidth; x++){
            for(int y = 0; y < map.mapHeight; y++){
                if(map.corridorSeen[y][x]){
                    TileType cell = map.getGrid(x,y);
                    if(cell != TileType.VOID){
                        Scene tile = new Scene(sceneAssetFloor.scene);
                        setTransform(tile.modelInstance.transform, x, y, 0, Direction.NORTH);
                        sceneManager.addScene(tile);
                    }
                }
            }
        }
    }

    public void populateMap(World world){
        for(Room room: world.map.rooms)
            if(room.uncovered)
                populateRoom(world, room);
    }

    public void populateRoom(World world, Room room){
        for(int x = room.x; x < room.x+room.width; x++){
            for(int y = room.y; y < room.y + room.height; y++){
                GameObject occupant = world.gameObjects.getOccupant(x,y);
                if(occupant != null){ // && occupant.type == GameObjectTypes.gold){
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
        setTransform(item.modelInstance.transform, gameObject.x, gameObject.y, 0, Direction.SOUTH);
        sceneManager.addScene(item);
        gameObject.scene = item;
    }

    public void placeRogue(World world){
        GameObject rogue = world.rogue;
        addScene(rogue);
        adaptModel(rogue.scene, Equipped.NONE);

        int roomId = world.map.roomCode[rogue.y][rogue.x];
        if(roomId >= 0) {
            Room room = world.map.rooms.get(roomId);
            room.uncovered = true;
        }
        else
            visitCorridorSegment(world.map, rogue.x, rogue.y);

    }


    // The next two methods should be the only place where we convert logical x,y to a transform
    //
    private void setTransform(Matrix4 transform, int x, int y, int z, Direction dir){
        transform.setToRotation(Vector3.Y, 180-dir.ordinal() * 90);
        transform.setTranslation(SCALE*x, z, -SCALE*y);

    }

    // leave orientation as it is
    private void setTransform(Matrix4 transform, int x, int y, int z){
        transform.setTranslation(SCALE*x, z, -SCALE*y);
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


    public void turnObject(GameObject go, Direction dir, int x, int y ){
        go.direction = dir;
        setTransform(go.scene.modelInstance.transform, x, y, go.z, dir);
    }

    public void moveObject( GameObject go, int x, int y, int z){
        go.x = x;
        go.y = y;
        go.z = z;

        setTransform(go.scene.modelInstance.transform, x, y, z);
    }

    public void remove(Scene scene){
        if(scene != null)
            sceneManager.removeScene(scene);
    }

    public void clear(){
        sceneManager.getRenderableProviders().clear();
    }

    @Override
    public void dispose() {
        sceneAssetFloor.dispose();
        sceneAssetWall.dispose();
        sceneAssetDoorWay.dispose();
        sceneAssetCorner.dispose();
        // todo
    }
}
