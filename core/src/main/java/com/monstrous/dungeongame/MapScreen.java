package com.monstrous.dungeongame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
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


public class MapScreen implements Screen {
    public final static int MAP_WIDTH = 80;
    public final static int MAP_HEIGHT = 60;
    public final static int MARGIN = 5;

    private int seed = 1234;
    private int level = 0;
    private DungeonMap map;
    private ShapeRenderer shapeRenderer;
    private OrthographicCamera camera;
    private FitViewport viewport;
    private Viewport uiViewport;
    private SpriteBatch batch;
    private BitmapFont font;
    private StringBuilder sb;

    @Override
    public void show() {
        map = new DungeonMap(seed, level, MAP_WIDTH, MAP_HEIGHT);
        shapeRenderer = new ShapeRenderer();
        camera = new OrthographicCamera();
        viewport = new FitViewport(MAP_WIDTH+2*MARGIN, MAP_HEIGHT+2*MARGIN, camera);
        uiViewport = new ScreenViewport();
        batch = new SpriteBatch();
        font = new BitmapFont();
        sb = new StringBuilder();
    }

    @Override
    public void render(float delta) {
        if(Gdx.input.isKeyJustPressed(Input.Keys.SPACE)){
            level++;
            System.out.println("seed: "+seed+ " level: "+level);
            map = new DungeonMap(seed, level, MAP_WIDTH, MAP_HEIGHT);
        }
        if(Gdx.input.isKeyJustPressed(Input.Keys.P)){
            System.out.println("Hello");
        }
        ScreenUtils.clear(Color.BLACK);
        shapeRenderer.setProjectionMatrix(camera.combined);

        // rooms
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.GREEN);
        for(Room room : map.rooms) {
            shapeRenderer.rect(room.x+MARGIN, room.y+MARGIN, room.width, room.height);
        }
        shapeRenderer.end();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        int m = MARGIN;

        // grid
//        shapeRenderer.setColor(Color.DARK_GRAY);
//        for(int x = 0; x <= MAP_WIDTH; x++){
//            shapeRenderer.line(x+m, m, x+m, MAP_HEIGHT+m);
//        }
//        for(int y = 0; y <= MAP_HEIGHT; y++){
//            shapeRenderer.line(m, y+m, MAP_WIDTH+m, y+m);
//        }


        shapeRenderer.setColor(Color.GRAY);
        for( int tri = 0; tri < map.indices.size; tri+= 3 ){
            int i1 = map.indices.get(tri);
            int i2 = map.indices.get(tri+1);
            int i3 = map.indices.get(tri+2);
            float x1 = map.vertices[2*i1];
            float y1 = map.vertices[2*i1+1];
            float x2 = map.vertices[2*i2];
            float y2 = map.vertices[2*i2+1];
            float x3 = map.vertices[2*i3];
            float y3 = map.vertices[2*i3+1];

            shapeRenderer.triangle(x1+m, y1+m, x2+m, y2+m, x3+m, y3+m);
        }

        // minimum spanning tree
        shapeRenderer.setColor(Color.RED);
        for(Room room: map.rooms){
            for(Room nbor : room.closeNeighbours) {
                shapeRenderer.line(room.centre.x + m, room.centre.y + m, nbor.centre.x + m, nbor.centre.y + m);
            }
        }
        shapeRenderer.end();


        // corridors & wall
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        for(int x = 0; x < MAP_WIDTH; x++){
            for(int y = 0; y < MAP_HEIGHT; y++) {
                TileType cell = map.getGrid(x,y);
                if(cell == TileType.CORRIDOR) {
                    shapeRenderer.setColor(Color.BLUE);
                    shapeRenderer.rect(x + m, y + m, 1, 1);
                }
                if(cell == TileType.WALL) {
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
                if(cell == TileType.STAIRS_UP) {
                    shapeRenderer.setColor(Color.BROWN);
                    shapeRenderer.rect(x + m, y + m, 1, 1);
                }
            }
        }
        shapeRenderer.end();


        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.GOLD);
        for(int x = 0; x < MAP_WIDTH; x++){
            for(int y = 0; y < MAP_HEIGHT; y++) {
                if(map.initialOccupancy[y][x] == GOLD) {
                    shapeRenderer.circle(x + m+.5f, y + m+.5f, 0.5f);
                }

            }
        }
        shapeRenderer.end();

        sb.setLength(0);
        sb.append("Map seed: ");
        sb.append(seed);
        sb.append(" Level: ");
        sb.append(level);
        batch.begin();
        font.draw(batch, sb.toString(), 10 ,90 );

        font.draw(batch, "Press SPACE to descend", 10 ,50 );

        batch.end();

    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        uiViewport.update(width, height, true);
        batch.setProjectionMatrix(uiViewport.getCamera().combined);
    }

    @Override
    public void pause() {
        // Invoked when your application is paused.
    }

    @Override
    public void resume() {
        // Invoked when your application is resumed after pause.
    }

    @Override
    public void hide() {
        // This method is called when another screen replaces this one.
    }

    @Override
    public void dispose() {
        // Destroy screen's assets here.
        shapeRenderer.dispose();
        map.dispose();
    }
}
