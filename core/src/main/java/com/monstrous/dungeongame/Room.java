package com.monstrous.dungeongame;

import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.utils.Array;

public class Room {
    public int id;              // unique id per room, = index in rooms Array
    public int x, y, width, height;
    public GridPoint2 centre;      // centre of the room
    public Array<Room> nbors;   // directly connected rooms after triangulation (temporary use)
    public Array<Float> distances;  // distance per neighbour (temporary use)
    public Array<Room> closeNeighbours;     // connected rooms from minimum spanning tree plus some extra ones for fun
    public boolean isStairWell;
    public TileType stairType;           // STAIRS_UP or STAIRS_DOWN (only valid if isStairWell)

    public Room(int id, int x, int y, int w, int h) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.width = w;
        this.height = h;
        centre = new GridPoint2(x+w/2, y+h/2);
        nbors = new Array<>();
        distances = new Array<>();
        closeNeighbours = new Array<>();
        isStairWell = false;
    }

    public boolean overlaps (Room r) {
        return x < r.x + r.width && x + width > r.x && y < r.y + r.height && y + height > r.y;
    }

    public void addNeighbour(Room nbor){
        if(!nbors.contains(nbor, true)) {  // avoid duplicates
            nbors.add(nbor);
            // Pythagoras to calculate distance between room centres
            float distance = (float)Math.sqrt(Math.pow(centre.x - nbor.centre.x, 2.0) + Math.pow(centre.y - nbor.centre.y, 2.0));
            distances.add(distance);
        }
    }

    public void addCloseNeighbour(Room nbor){
        closeNeighbours.add(nbor);
    }
}
