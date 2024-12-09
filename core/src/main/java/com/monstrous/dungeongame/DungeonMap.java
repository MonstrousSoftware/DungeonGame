package com.monstrous.dungeongame;

import com.badlogic.gdx.math.DelaunayTriangulator;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.ShortArray;



// This implements an algorithm described by Vazgriz (only for the 2D case) to generate dungeons
//
// https://vazgriz.com/119/procedurally-generated-dungeons/
// https://www.youtube.com/watch?v=rBY2Dzej03A
//
// 1. Place rooms at random
// 2. Use Delaunay triangulator to connect the rooms
// 3. Find minimum spanning tree to find a minimal tree to connect all rooms
// 4. Add some random edges to make it less minimal and to allow for some loops
// 5. Use A* to turn edges into corridors with a bias to join to existing corridors


public class DungeonMap implements Disposable {
    public static final int MIN_SIZE = 3;           // min size of room
    public static final int MAX_SIZE = 10;          // max size of room
    public static final float LOOP_FACTOR = 0.125f; // probability factor [0..1] to add some extra non-MST edge to paths

    private final int mapSeed;
    public final int mapWidth, mapHeight;
    public final Array<Room> rooms;
    public int roomId;                      // to give each room a unique id
    public float[] vertices;                // array of x,y per room centre
    public ShortArray indices;              // index list from triangulation
    private TileType [][] grid;             // map grid for fixed architecture, walls, etc.
    public Direction [][] tileOrientation;      // orientation of tile
//    public GameObjects gameObjects;


    // levelNr : 0 for top level, increasing as we go down

    public DungeonMap(int mapSeed, int levelNr, int width, int height) {
        this.mapSeed = mapSeed;
        this.mapWidth = width;
        this.mapHeight = height;
        rooms = new Array<>();


        roomId = 0;
        rooms.clear();

        if(levelNr > 0) {    // is there a floor above?
            // use the higher level's seed to generate the stairs from above
            MathUtils.random.setSeed(getLevelSeed(mapSeed, levelNr-1));
            generateStairWells(TileType.STAIRS_UP);  // stairs coming down
        }

        MathUtils.random.setSeed(getLevelSeed(mapSeed, levelNr));

        // generate stairs to the level below
        generateStairWells(TileType.STAIRS_DOWN);    // stairs going down

        generateRooms(rooms);

        addGraph(rooms);

        connectRooms();

        findMinimumSpanningTree();

        addLoopEdges();

        fillGrid();

        makeCorridors();

        //addCorridorWalls();

//        gameObjects = new GameObjects(mapWidth, mapHeight);
//
//        placeRogue();
//        distributeGold();
    }

    // derive seed for a specific level of a map
    private int getLevelSeed(int mapSeed, int level){
        return 100* mapSeed + level;
    }

    public TileType getGrid(int x, int y){
        return grid[y][x];
    }

    // generate non-overlapping rooms of random size and position until the map is pretty full
    private void generateRooms(Array<Room> rooms){

        int attempts = 0;
        while(attempts < 40) {       // stop after N attempts to place a random room, the map must be quite full
            Room room = generateRoom(roomId);
            boolean overlap = checkOverlap(room, rooms);
            if(!overlap) {
                rooms.add(room);
                attempts = 0;
                roomId++;
            }
            else
                attempts++;
        }
    }


    private Room generateRoom(int id){
        int w = MathUtils.random(MIN_SIZE, MAX_SIZE);
        int h = MathUtils.random(MIN_SIZE, MAX_SIZE);
        return placeRoom(id, w, h);
    }

    // place some stair wells going down
    private void generateStairWells(TileType stairType){
        int count = MathUtils.random(1, 2); // how many stair wells to generate?
        for(int i = 0; i < count; i++){
            Room stairWell = generateStairWell(roomId++, stairType);
            stairWell.stairType = stairType;
            rooms.add(stairWell);
        }
    }

    // a stair well is a special type of room of fixed size with stair tiles inside.
    private Room generateStairWell(int id, TileType stairType){
        int d = MathUtils.random(0, 3); // random direction NESW
        Direction direction = Direction.values()[d];
        // place horizontal or vertical
        int w = (direction == Direction.EAST || direction == Direction.WEST) ? 3 : 1;
        int h = (direction == Direction.EAST || direction == Direction.WEST) ? 1 : 3;
        Room stairWell = placeRoom(id, w, h);
        if(stairType == TileType.STAIRS_UP) {
            d = (d + 2) % 4;    // reverse direction
            direction = Direction.values()[d];
            // offset stairwell one cell compared to the floor above
            switch(direction){
                case NORTH: stairWell.y++; break;
                case EAST: stairWell.x--; break;
                case SOUTH: stairWell.y--; break;
                case WEST: stairWell.x++; break;
            }
        }
        switch(direction) {
            case SOUTH:     // pointing south
            case EAST:     // pointing east
                stairWell.centre.set(stairWell.x, stairWell.y); // centre connection node on the landing
                break;
            case NORTH:     // pointing north
                stairWell.centre.set(stairWell.x, stairWell.y+2); // centre connection node on the landing
                break;
            case WEST:     // pointing west
                stairWell.centre.set(stairWell.x+2, stairWell.y); // centre connection node on the landing
                break;
        }
        stairWell.isStairWell = true;
        return stairWell;
    }

    private Room placeRoom(int id, int w, int h){
        // leave three cell margin from the edge of the map for the walls to go and to allow for corridors on the outside with a wall of its own
        int x = MathUtils.random(3, mapWidth - (w+4));
        int y = MathUtils.random(3, mapHeight - (h+4));
        return new Room(id, x, y, w, h);
    }

    private boolean checkOverlap(Room newRoom, Array<Room> rooms){

        // add a 1 unit boundary around the new room to leave room for walls
        Room expandedRoom = new Room(-1, newRoom.x-1, newRoom.y-1,
                                newRoom.width+2, newRoom.height+2);

        for(Room room : rooms ){
            if(room.overlaps(expandedRoom))
                return true;
        }
        return false;
    }

    // Triangulate all room centres using Delaunay
    // The triangulation is represented by vertices + indices.
    private void addGraph(Array<Room> rooms){
        DelaunayTriangulator triangulator = new DelaunayTriangulator();

        vertices = new float[2*rooms.size];
        int index = 0;
        for(Room room : rooms ){
            vertices[index++] = room.centre.x;
            vertices[index++] = room.centre.y;
        }

        indices = triangulator.computeTriangles(vertices, 0, 2*rooms.size, false);
    }

    // Use the triangulation data to connect joining rooms
    // This translates the graphical triangle data to logical node connections
    private void connectRooms(){
        for( int tri = 0; tri < indices.size; tri+= 3 ){
            int i1 = indices.get(tri);
            int i2 = indices.get(tri+1);
            int i3 = indices.get(tri+2);
            float x1 = vertices[2*i1];
            float y1 = vertices[2*i1+1];
            float x2 = vertices[2*i2];
            float y2 = vertices[2*i2+1];
            float x3 = vertices[2*i3];
            float y3 = vertices[2*i3+1];

            Room r1 = findRoomByPosition(x1, y1);
            Room r2 = findRoomByPosition(x2, y2);
            Room r3 = findRoomByPosition(x3, y3);
            r1.addNeighbour(r2);
            r1.addNeighbour(r3);
            r2.addNeighbour(r1);
            r2.addNeighbour(r3);
            r3.addNeighbour(r1);
            r3.addNeighbour(r2);

        }
    }

    private Room findRoomByPosition(float x, float y){
        for(Room room : rooms ){
            if( MathUtils.isEqual(room.centre.x, x, 0.1f) &&  MathUtils.isEqual(room.centre.y, y, 0.1f))
                return room;
        }
        throw new RuntimeException("Room cannot be found by position");
    }

    private void findMinimumSpanningTree(){
        // Using Prim-Dijkstra algorithm
        //
        Array<Room> connected = new Array<>();      // the tree, initially empty
        Array<Room> unconnected = new Array<>();    // nodes not in the tree, initially all of them
        for(Room room : rooms )
            unconnected.add(room);

        // choose random room to start with
        int root = MathUtils.random(0, rooms.size-1);


        Room closestRoom = rooms.get(root);

        while(closestRoom != null) {

            // move room from unconnected to connected, because now it is in the tree
            unconnected.removeValue(closestRoom, true);
            connected.add(closestRoom);


            // find unconnected room with the smallest distance to (any branch in) the tree
            closestRoom = null;
            Room connectingBranch = null;
            float smallestDistance = Float.MAX_VALUE;
            for (Room node : connected) {
                for (int i = 0; i < node.nbors.size; i++) {
                    Room nbor = node.nbors.get(i);
                    if (connected.contains(nbor, true))  // neighbour already in the tree? skip
                        continue;
                    float distance = node.distances.get(i);
                    if (distance < smallestDistance) {    // closest so far
                        smallestDistance = distance;
                        closestRoom = nbor;
                        connectingBranch = node;
                    }
                }
            }
            if(closestRoom == null)
                break;

            // store the link from both sides
            closestRoom.addCloseNeighbour(connectingBranch);
            connectingBranch.addCloseNeighbour(closestRoom);
        }
    }

    // Add some random edges from the triangulation to the ones selected by the minimum spanning tree
    // to allow for some loops and more interesting connectivity.
    //
    private void addLoopEdges(){
        for(Room room : rooms ){
            for(Room nbor : room.nbors ){
                if(room.closeNeighbours.contains(nbor, true))       // is already a close neighbour
                    continue;
                if(MathUtils.random(1.0f) < LOOP_FACTOR) { // with some probability
                    room.addCloseNeighbour(nbor);
                    nbor.addCloseNeighbour(room);
                }
            }
        }
    }

    private void fillGrid(){
        grid = new TileType[mapHeight][mapWidth];
        tileOrientation = new Direction[mapHeight][mapWidth];
        for(int x = 0; x < mapWidth; x++) {
            for (int y = 0; y < mapHeight; y++) {
                grid[y][x] = TileType.VOID;
                tileOrientation[y][x] = Direction.NORTH;
            }
        }

        for(Room room : rooms ){
            addRoom(room.x, room.y, room.width, room.height);
            // todo stairwells
//            for(int x =  room.x; x < room.x+room.width; x++){
//                for(int y =  room.y; y < room.y+room.height; y++){
//                    grid[y][x] = room.isStairWell ? (short)room.stairType : ROOM;
//                }
//            }

        }
    }

    private void addRoom(int rx, int ry, int rw, int rh){

        for(int x = 0; x < rw; x++){
            for(int y = 0; y < rh; y++){
                grid[ry+y][rx+x] = TileType.ROOM;
            }
        }

        for(int x = 0; x < rw; x++){
            grid[ry][rx+x] = TileType.WALL;
            grid[ry+rh][rx+x] = TileType.WALL;
        }
        for(int y = 0; y < rh; y++){
            grid[ry+y][rx] = TileType.WALL; tileOrientation[ry+y][rx] = Direction.EAST;
            grid[ry+y][rx+rw] = TileType.WALL; tileOrientation[ry+y][rx+rw] = Direction.EAST;
        }
        // rotate the wall corner model as needed
        //
        //    E---S
        //    |   |
        //    N---W
        //
        grid[ry][rx] = TileType.WALL_CORNER; tileOrientation[ry][rx] = Direction.NORTH;
        grid[ry+rh][rx] = TileType.WALL_CORNER; tileOrientation[ry+rh][rx] = Direction.EAST;
        grid[ry][rx+rw] = TileType.WALL_CORNER; tileOrientation[ry][rx+rw] = Direction.WEST;
        grid[ry+rh][rx+rw] = TileType.WALL_CORNER; tileOrientation[ry+rh][rx+rw] = Direction.SOUTH;
    }

    private void addDoor(int x, int y, Direction direction){
        grid[y][x] = TileType.DOORWAY; tileOrientation[y][x] = direction;
    }

    private void makeCorridors(){
        for(Room room : rooms){
            for(Room nbor : room.closeNeighbours){
                if(room.id < nbor.id)   // avoid doing edges twice
                    makeCorridor(room, nbor);
            }
        }
    }

    private static class Node{
        int x, y;
        int cost;
        Node parent;

        public Node(int x, int y, int cost, Node parent) {
            this.x = x;
            this.y = y;
            this.cost = cost;
            this.parent = parent;
        }
    }

    private final int[] dx = { 0, 1, 0, -1 };
    private final int[] dy = { 1, 0, -1, 0 };

    private void makeCorridor(Room A, Room B){
        int x = A.centre.x;
        int y = A.centre.y;
        int targetX = B.centre.x;
        int targetY = B.centre.y;
        Node root = new Node(x, y, 0, null);
        Array<Node> closed = new Array<>();
        Array<Node> fringe = new Array<>();

        fringe.add(root);

        while(fringe.size > 0){

            // find closest node in the fringe
            int minCost = Integer.MAX_VALUE;
            Node current = null;
            for(Node n : fringe){
                if(n.cost < minCost){
                    minCost = n.cost;
                    current = n;
                }
            }
            assert current != null;

            // did we reach the goal?
            if(current.x == targetX && current.y == targetY) { // found target
                // back trace the steps and update the grid
                while(current != null) {
                    if (grid[current.y][current.x] == TileType.VOID)
                        grid[current.y][current.x] = TileType.CORRIDOR;
                    if (grid[current.y][current.x] == TileType.WALL  )
                        grid[current.y][current.x] = TileType.DOORWAY;
                    //TileType cell = grid[current.y][current.x];
                    current = current.parent;
                }
                return; // finished
            }

            // remove node from fring and add it to the closed set
            fringe.removeValue(current, true);
            closed.add(current);

            // for each neighbour of current
            for(int dir = 0; dir < 4; dir++){
                int nx = current.x + dx[dir];
                int ny = current.y + dy[dir];
                if(nx < 0 || nx >= mapWidth || ny < 0 || ny >= mapHeight)
                    continue;

                // if neighbour in closed set, skip it
                boolean found = false;
                for(Node n : closed){
                    if(n.x == nx && n.y == ny) {
                        found = true;
                        break;
                    }
                }
                if(found)
                    continue;

                // calculate cost to neighbour via current
                int cost = current.cost;
                switch(grid[ny][nx]){
                    case VOID:     cost += 5; break;
                    case ROOM:      cost += 10; break;
                    case CORRIDOR:  cost += 1; break;
                    case WALL:      cost += 20; break;
                    case STAIRS_DOWN:    cost += 100; break;        // never path via a staircase
                    case STAIRS_UP:      cost += 100; break;
                }
                Node nbor = null;
                for(Node n : fringe){
                    if(n.x == nx && n.y == ny) {
                        nbor = n;
                        break;
                    }
                }
                if(nbor == null) {
                    nbor = new Node(nx, ny, cost, current);
                    fringe.add(nbor);
                } else {
                    if(cost < nbor.cost){
                        nbor.cost = cost;       // found a shorter path for neighbour
                        nbor.parent = current;
                    }
                }
            }
        }
    }

    private final int[] ddx = { -1 , 0, 1, -1, 1, -1, 0, 1 };
    private final int[] ddy = { -1, -1, -1, 0, 0, 1, 1, 1 };


    // put walls around corridors where necessary, i.e. where next to an empty cell, including diagonals
    private void addCorridorWalls(){
        for(int x = 0; x < mapWidth; x++){
            for(int y = 0; y < mapHeight; y++){
                if(grid[y][x] == TileType.CORRIDOR){
                    for(int dir = 0; dir < 8; dir++){
                        if(grid[y+ddy[dir]][x+ddx[dir]] == TileType.VOID) {
                            if(dir == 1 || dir == 3 || dir == 4 || dir == 6){
                                grid[y + ddy[dir]][x + ddx[dir]] = TileType.WALL;
                                if(dir == 3 || dir ==4)
                                    tileOrientation[y + ddy[dir]][x + ddx[dir]] = Direction.EAST;
                            }
//                            else {
//                                grid[y + ddy[dir]][x + ddx[dir]] = CORNER;
//                            }
//                            if(dir == 0 || dir == 3 || dir == 4 || dir == 5)
//                                orientation[y + ddy[dir]][x + ddx[dir]] = 1;

                        }
                    }
                }
            }
        }
    }

//    private void distributeGold(){
//
//        int count = MathUtils.random(10, 25);        // nr of gold drops
//        while(true){
//            int location = MathUtils.random(0, rooms.size-1);
//            Room room = rooms.get(location);
//            if(room.isStairWell)
//                continue;
//            int rx = MathUtils.random(0, room.width-1);
//            int ry = MathUtils.random(0, room.height-1);
//            GameObject occupant = gameObjects.getOccupant(room.x+rx, room.y+ry);
//            if(occupant != null)
//                continue;
//
//            occupant = new GameObject(GameObjectTypes.gold, room.x+rx, room.y+ry, Direction.SOUTH);
//            gameObjects.setOccupant(room.x+rx, room.y+ry, occupant);
//            gameObjects.add(occupant);
//            occupant.goldQuantity = MathUtils.random(1,20);
//            // seems redundant to provide x,y twice
//
//
//            count--;
//            if(count == 0)
//                return;
//        }
//    }
//
//    private void placeRogue(){
//        while(true) {
//            int location = MathUtils.random(0, rooms.size-1);
//            Room room = rooms.get(location);
//            if(room.isStairWell)
//                continue;
//            GameObject occupant = gameObjects.getOccupant(room.centre.x, room.centre.y);
//            if(occupant != null)
//                continue;
//
//            occupant = new GameObject(GameObjectTypes.rogue, room.centre.x, room.centre.y, Direction.SOUTH);
//            gameObjects.setOccupant(room.centre.x, room.centre.y, occupant);
//            gameObjects.add(occupant);
//            occupant.direction = Direction.SOUTH;
//
//            return;
//        }
//    }

    @Override
    public void dispose() {
        indices.clear();
    }
}
