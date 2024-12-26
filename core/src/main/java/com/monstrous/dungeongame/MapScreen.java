package com.monstrous.dungeongame;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import static com.monstrous.dungeongame.DungeonMap.*;


public class MapScreen extends StdScreenAdapter {
    //public final static int MARGIN = 5;

    private final Main game;
    private final World world;
    private ShapeRenderer shapeRenderer;
    private OrthographicCamera camera;
    private FitViewport viewport;
    private SpriteBatch batch;
    private BitmapFont font;
    private StringBuilder sb;

    public MapScreen(Main game) {
        this.game = game;
        this.world = game.world;
    }

    @Override
    public void show() {
        shapeRenderer = new ShapeRenderer();
        camera = new OrthographicCamera();
        viewport = new FitViewport(world.map.mapWidth, world.map.mapHeight, camera);
        batch = new SpriteBatch();
        font = new BitmapFont();
        sb = new StringBuilder();
    }

    @Override
    public void render(float deltaTime) {
        super.render(deltaTime);

        if(Gdx.input.isKeyJustPressed(Input.Keys.M)){
            game.setScreen( new PreGameScreen(game) );
            return;
        }

        if(Gdx.input.isKeyJustPressed(Input.Keys.L)){
            world.levelDown();
            viewport.setWorldSize(world.map.mapWidth, world.map.mapHeight);
            viewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
            System.out.println("seed: "+world.seed+ " level: "+world.level);
        }
        if(Gdx.input.isKeyJustPressed(Input.Keys.K)){
            world.levelUp();
            viewport.setWorldSize(world.map.mapWidth, world.map.mapHeight);
            viewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
            System.out.println("seed: "+world.seed+ " level: "+world.level);
        }
        if(game.keyController != null)
            game.keyController.update(deltaTime); // for key repeat

        viewport.setWorldSize(world.map.mapWidth, world.map.mapHeight);
        viewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);

        ScreenUtils.clear(Color.BLACK);
        shapeRenderer.setProjectionMatrix(camera.combined);

        int m = 0;

        // rooms
//        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
//        shapeRenderer.setColor(Color.GREEN);
//        for(Room room : map.rooms) {
//            shapeRenderer.rect(room.x+m, room.y+m, room.width, room.height);
//        }
//        shapeRenderer.end();

//
//

        // grid
        boolean showGrid = false;
        if(showGrid) {
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            shapeRenderer.setColor(Color.DARK_GRAY);
            for (int x = 0; x <= world.map.mapWidth; x++) {
                shapeRenderer.line(x + m, m, x + m, world.map.mapHeight + m);
            }
            for (int y = 0; y <= world.map.mapHeight; y++) {
                shapeRenderer.line(m, y + m, world.map.mapWidth + m, y + m);
            }
            shapeRenderer.end();
        }


//        shapeRenderer.setColor(Color.GRAY);
//        for( int tri = 0; tri < map.indices.size; tri+= 3 ){
//            int i1 = map.indices.get(tri);
//            int i2 = map.indices.get(tri+1);
//            int i3 = map.indices.get(tri+2);
//            float x1 = map.vertices[2*i1];
//            float y1 = map.vertices[2*i1+1];
//            float x2 = map.vertices[2*i2];
//            float y2 = map.vertices[2*i2+1];
//            float x3 = map.vertices[2*i3];
//            float y3 = map.vertices[2*i3+1];
//
//            shapeRenderer.triangle(x1+m, y1+m, x2+m, y2+m, x3+m, y3+m);
//        }



        // corridors & wall
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        for(int x = 0; x < world.map.mapWidth; x++){
            for(int y = 0; y < world.map.mapHeight; y++) {
                TileType cell = world.map.getGrid(x,y);
                if(cell == TileType.ROOM) {
                    shapeRenderer.setColor(Color.GREEN);
                    shapeRenderer.rect(x + m, y + m, 1, 1);
                }
                if(cell == TileType.CORRIDOR) {
                    shapeRenderer.setColor(Color.BLUE);
                    shapeRenderer.rect(x + m, y + m, 1, 1);
                }
                if(cell == TileType.WALL || cell == TileType.WALL_T_SPLIT || cell == TileType.WALL_CORNER ||
                    cell == TileType.WALL_CROSSING) {
                    shapeRenderer.setColor(Color.DARK_GRAY);
                    shapeRenderer.rect(x + m, y + m, 1, 1);
                }
                if(cell == TileType.DOORWAY) {
                    shapeRenderer.setColor(Color.PURPLE);
                    shapeRenderer.rect(x + m, y + m, 1, 1);
                }
                if(cell == TileType.STAIRS_DOWN) {
                    shapeRenderer.setColor(Color.RED);
                    shapeRenderer.rect(x + m, y + m, 1, 1);
                }
                if(cell == TileType.STAIRS_DOWN_DEEP) {
                    shapeRenderer.setColor(0.5f, 0f, 0f, 1.0f);
                    shapeRenderer.rect(x + m, y + m, 1, 1);
                }
                if(cell == TileType.STAIRS_UP) {
                    shapeRenderer.setColor(Color.BROWN);
                    shapeRenderer.rect(x + m, y + m, 1, 1);
                }
                if(cell == TileType.STAIRS_UP_HIGH) {
                    shapeRenderer.setColor(Color.ORANGE);
                    shapeRenderer.rect(x + m, y + m, 1, 1);
                }
            }
        }
        shapeRenderer.end();

        // Rogue character
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(38/255f, 0.5f, 0, 1f);
        shapeRenderer.circle(world.rogue.x + m+.5f, world.rogue.y + m+.5f, 0.5f);
        shapeRenderer.end();

        // enemies
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(200/255f, 0.2f, 0.2f, 1f);
        for(GameObject enemy : world.enemies.enemies) {
            shapeRenderer.circle(enemy.x + m + .5f, enemy.y + m + .5f, 0.5f);
        }
        shapeRenderer.end();

        // goodies
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.ORANGE);
        for(GameObject item : world.levelData.gameObjects.gameObjects) {
            if(!item.type.isEnemy  && !item.type.isPlayer)
                shapeRenderer.circle(item.x + m + .5f, item.y + m + .5f, 0.3f);
        }
        shapeRenderer.end();

        // minimum spanning tree
//        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
//        shapeRenderer.setColor(Color.RED);
//        for(Room room: world.map.rooms){
//            for(Room nbor : room.closeNeighbours) {
//                shapeRenderer.line(room.centre.x + m, room.centre.y + m, nbor.centre.x + m, nbor.centre.y + m);
//            }
//        }
//        shapeRenderer.end();


        sb.setLength(0);
        sb.append("Map seed: ");
        sb.append(world.seed);
        sb.append(" Level: ");
        sb.append(world.level);

        batch.begin();
        font.draw(batch, sb.toString(), 10 ,90 );
        font.draw(batch, "Press M to return", 10 ,50 );
        batch.end();

    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }


    @Override
    public void hide() {
        // This method is called when another screen replaces this one.
    }

    @Override
    public void dispose() {
        // Destroy screen's assets here.
        shapeRenderer.dispose();
        batch.dispose();
    }
}
