package com.monstrous.dungeongame;

import net.mgsx.gltf.scene3d.scene.Scene;

public class GameObject {

    public GameObjectType type;
    public int x,y;
    public int z;       // above/below ground level, e.g. when walking stairs
    public Direction direction;
    public Scene scene;
    public CharacterStats stats;
    public int quantity;            // e.g. amount of gold for a gold object


    public GameObject(GameObjectType type, int x, int y, Direction direction) {
        this.type = type;
        this.x = x;
        this.y = y;
        this.direction = direction;
        this.quantity = 0;
    }
}
