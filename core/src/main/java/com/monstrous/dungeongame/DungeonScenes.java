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

public class DungeonScenes implements Disposable {
    private final static float SCALE = 4f;

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
    private SceneAsset sceneAssetPillar;


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
        sceneAssetPillar = new GLTFLoader().load(Gdx.files.internal("models/pillar.gltf"));
    }

    public void showMap(DungeonMap map){
        for(Room room: map.rooms)
            if(room.uncovered)
                showRoom(map, room);

    }

    public void showRoom(DungeonMap map, Room room){
        room.uncovered = true;

        for(int x = room.x; x <= room.x + room.width; x++){
            for(int y = room.y; y <= room.y + room.height; y++){
                map.tileSeen[y][x] = true;

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
                    tile = new Scene(sceneAssetWallCrossing.scene);
                    z = -8;
                    setTransform(tile.modelInstance.transform, x, y, z, map.tileOrientation[y][x]);
                    sceneManager.addScene(tile);
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
                    tile = new Scene(sceneAssetWallCrossing.scene);
                    setTransform(tile.modelInstance.transform, x, y, z, map.tileOrientation[y][x]);
                    sceneManager.addScene(tile);
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
    public void visitCorridorSegment(World world, int x, int y){
        //unmaskCorridorSegment(world, x, y);
        for(int dx = -1; dx <= 1; dx++){
            for(int dy = -1; dy <= 1; dy++){
                unmaskCorridorSegment(world, x+dx, y+dy);
            }
        }
    }

    public void unmaskCorridorSegment(World world, int x, int y){
        if(world.map.tileSeen[y][x])
            return;
        if( !TileType.hasFloor(world.map.getGrid(x,y)))
            return;

        world.map.tileSeen[y][x] = true;

        Scene tile = new Scene(sceneAssetFloor.scene);
        setTransform(tile.modelInstance.transform, x, y, 0, Direction.NORTH);
        sceneManager.addScene(tile);

        TileType cell = world.map.getGrid(x,y);
        if(cell == TileType.DOORWAY){
            Scene doorway = new Scene(sceneAssetDoorWay.scene);
            setTransform(doorway.modelInstance.transform, x, y, 0, world.map.tileOrientation[y][x]);
            sceneManager.addScene(doorway);
        }

        GameObject occupant = world.gameObjects.getOccupant(x,y);
        if(occupant != null && occupant.scene == null){
            addScene(occupant);
        }
    }

    public void buildCorridors(DungeonMap map){
        for(int x = 0; x < map.mapWidth; x++){
            for(int y = 0; y < map.mapHeight; y++){
                if(map.tileSeen[y][x]){
                    TileType cell = map.getGrid(x,y);
                    if(TileType.hasFloor(cell)){
                        Scene tile = new Scene(sceneAssetFloor.scene);
                        setTransform(tile.modelInstance.transform, x, y, 0, Direction.NORTH);
                        sceneManager.addScene(tile);
                    }
                }
            }
        }
    }

    public void populateMap(World world){
        for(GameObject enemy: world.enemies.enemies)
            enemy.scene = null;

        for(Room room: world.map.rooms)
            if(room.uncovered)
                populateRoom(world, room);
    }

    public void populateRoom(World world, Room room){
        for(int x = room.x; x < room.x+room.width; x++){
            for(int y = room.y; y < room.y + room.height; y++){
                GameObject occupant = world.gameObjects.getOccupant(x,y);
                if(occupant != null && occupant.scene == null){
                    // note monsters could be seen before outside this room
                    addScene(occupant);
                }
            }
        }
    }

    private int[] dx = { 0, -1, 1, 0, 0, -1, -1, 1, 1 };
    private int[] dy = { 0, 0, 0, -1, 1, 1, -1, 1, -1 };

    // drop item at location x,y
    // if there is already something there of the same type, add it to the pile
    // if there is something else there try a nearby tile
    //
    public void dropObject(DungeonMap map, GameObjects gameObjects, GameObject item, int x, int y){

        for(int offset = 0; offset < 9; offset++){
            int tx = x+dx[offset];
            int ty = y+dy[offset];
            TileType tile = map.getGrid(tx, ty);
            if(!TileType.droppable(tile))       // don't drop item inside a wall, etc.
                continue;
            GameObject occupant = gameObjects.getOccupant(tx,ty);
            // anything already there?
            if(occupant == null) {  // empty spot
                placeObject(gameObjects, item, tx, ty);
                return;
            }
            else if(occupant.type == item.type || occupant.type.isArrow && item.type.isArrow) {   // same type
                occupant.quantity+= item.quantity;        // add to the pile
                if(occupant.type.isArrow && occupant.quantity > 0){ // change single arrow to bundle
                    removeScene(occupant);
                    occupant.type = GameObjectTypes.arrows;
                    addScene(occupant);
                }
                return;
            }
        }
        // can't place
        MessageBox.addLine(item.type.name+ " dropped and disappeared.");
    }

    public void placeObject(GameObjects gameObjects, GameObjectType type, int x, int y){
        GameObject go = new GameObject(type, x, y, Direction.SOUTH);
        placeObject(gameObjects, go, x, y);
    }

    public void placeObject(GameObjects gameObjects, GameObject item, int x, int y){
        item.x = x;
        item.y = y;
        item.z = item.type.z;

        addScene(item);
        gameObjects.add(item);
        if(!item.type.isPlayer)
            gameObjects.setOccupant(x, y, item);
        // how to handle enemies walking over gold etc.
    }

    public void addScene(GameObject gameObject){

        Scene item = new Scene(gameObject.type.sceneAsset.scene);
        setTransform(item.modelInstance.transform, gameObject.x, gameObject.y, gameObject.z, gameObject.direction);
        sceneManager.addScene(item);
        gameObject.scene = item;
    }


    public void removeScene(GameObject gameObject){
        sceneManager.removeScene(gameObject.scene);
        gameObject.scene = null;
    }

    public void createRogueModel(World world){
        GameObject rogue = world.rogue;
        addScene(rogue);
        adaptModel(rogue.scene, rogue.stats);
    }

    public void liftFog(World world){
        int roomId = world.map.roomCode[world.rogue.y][world.rogue.x];
        if(roomId >= 0) {
            Room room = world.map.rooms.get(roomId);
            room.uncovered = true;
        }
        else
            visitCorridorSegment(world, world.rogue.x, world.rogue.y);
    }


    // The next two methods should be the only place where we convert logical x,y to a transform
    //
    private void setTransform(Matrix4 transform, int x, int y, float z, Direction dir){
        transform.setToRotation(Vector3.Y, 180-dir.ordinal() * 90);
        transform.setTranslation(SCALE*x, z, -SCALE*y);

    }

    // leave orientation as it is
    private void setTransform(Matrix4 transform, int x, int y, float z){
        transform.setTranslation(SCALE*x, z, -SCALE*y);
    }

    public void adaptModel(Scene rogue, CharacterStats stats){
        ModelInstance instance = rogue.modelInstance;
        for(Node node : instance.nodes){
            checkNode(1, node, stats.weaponItem);
        }
    }

    // recursive method to enable/disable weapons
    private void checkNode(int level, Node node, GameObject weapon ){
        //Gdx.app.log("Node", "level "+ level + " : "+node.id+ " nodeparts: "+node.parts.size);
        if(node.id.contentEquals("Knife"))
            setNodeParts(node, (weapon != null && weapon.type == GameObjectTypes.knife));
        else if(node.id.contains("Knife_Offhand"))
            setNodeParts(node, false);
        else if(node.id.contains("Crossbow"))
            setNodeParts(node, (weapon != null && weapon.type == GameObjectTypes.crossbow));
        else if(node.id.contains("Throwable"))
            setNodeParts(node, (weapon != null && weapon.type == GameObjectTypes.explosive));

        for(Node n : node.getChildren()){
            checkNode(level+1, n, weapon);
        }
    }

    private void setNodeParts(Node node, boolean enabled){
        for(NodePart part : node.parts)
            part.enabled = enabled;
    }


    public void turnObject(GameObject go, Direction dir, int x, int y ){
        go.direction = dir;
        if(go.scene != null)
            setTransform(go.scene.modelInstance.transform, x, y, go.z, dir);
    }

    public void moveObject(GameObject go, int x, int y, float z){
        go.x = x;
        go.y = y;
        go.z = z;

        if(go.scene != null)    // characters can move offscreen, only update model instance if it exists
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
