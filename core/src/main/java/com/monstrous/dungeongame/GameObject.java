package com.monstrous.dungeongame;

import net.mgsx.gltf.scene3d.scene.Scene;

public class GameObject {

    public GameObjectType type;
    public int x,y;
    public Direction direction;
    public Scene scene;
    public int equipped;
    public int goldQuantity;

    public GameObject(GameObjectType type, int x, int y, Direction direction) {
        this.type = type;
        this.x = x;
        this.y = y;
        this.direction = direction;
        this.goldQuantity = 0;
    }
}
