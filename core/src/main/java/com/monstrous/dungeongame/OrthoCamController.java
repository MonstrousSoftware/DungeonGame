package com.monstrous.dungeongame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;

// isometric camera controller


public class OrthoCamController extends InputAdapter {

    public final static float MAX_ZOOM = 0.01f;
    public final static float ZOOM_SPEED = 0.1f;

    private OrthographicCamera cam;
    private Vector3 position;
    private Vector3 prevPosition;
    private boolean zoomInPressed = false;
    private boolean zoomOutPressed = false;

    public OrthoCamController(OrthographicCamera cam) {
        this.cam = cam;
        position = new Vector3();
        prevPosition = new Vector3();
    }

    public void update( float deltaTime, ModelInstance trackedObject ) {
        boolean mustUpdate = false;
        if(zoomInPressed) {
            zoom(ZOOM_SPEED * deltaTime);
            mustUpdate = true;
        }
        else if(zoomOutPressed) {
            zoom(-ZOOM_SPEED * deltaTime);
            mustUpdate = true;
        }

        trackedObject.transform.getTranslation(position);
        if( ! position.epsilonEquals(prevPosition )) {
            cam.position.add(position).sub(prevPosition);
            prevPosition.set(position);
            mustUpdate = true;
        }

        if(mustUpdate)
            cam.update();
    }

    private float mx,my;

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        mx = screenX;
        my = screenY;
        return true;
    }

    private Vector3 dpos = new Vector3();

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        float dx = screenX - mx;
        float dy = screenY - my;

        dpos.x = dx;
        dpos.z = dy;
        dpos.y = 0;
        dpos.rotate(Vector3.Y, 45);
        dpos.scl(cam.zoom);

        cam.position.add(dpos);
        cam.update();
        mx = screenX;
        my = screenY;

        return true;
    }

    @Override
    public boolean scrolled (float amountX, float amountY) {
        zoom(amountY * 0.1f * ZOOM_SPEED );
        cam.update();
        return true;
    }

    public void zoom (float amount) {
        cam.zoom += amount;
        if(cam.zoom < MAX_ZOOM)
            cam.zoom = MAX_ZOOM;

        //Gdx.app.log("zoom", ""+cam.zoom);
    }

    @Override
    public boolean keyDown (int keycode) {
        setKeyState(keycode, true);
        return false;
    }

    @Override
    public boolean keyUp (int keycode) {
        setKeyState(keycode, false);
        return false;
    }

    private void setKeyState(int keycode, boolean state) {
        if (keycode == Input.Keys.EQUALS)
            zoomInPressed = state;
        else if (keycode == Input.Keys.MINUS)
            zoomOutPressed = state;
    }
}
