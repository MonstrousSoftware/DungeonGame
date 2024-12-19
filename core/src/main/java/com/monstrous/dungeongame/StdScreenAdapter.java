package com.monstrous.dungeongame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;

public class StdScreenAdapter extends ScreenAdapter {
    private static int width;           // static so it is retained between different screens
    private static int height;

    @Override
    public void render(float delta) {
        super.render(delta);

        // Use F11 key to toggle full screen / windowed screen
        if (Gdx.input.isKeyJustPressed(Input.Keys.F11)) {               // wait till key is released so we toggle only once
            if (!Gdx.graphics.isFullscreen()) {
                width = Gdx.graphics.getWidth();
                height = Gdx.graphics.getHeight();
                Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
                Gdx.app.log("To fullscreen", "from "+width+" x "+height);
                resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            } else {
                Gdx.graphics.setWindowedMode(width, height);
                Gdx.app.log("To windowed mode", "" + width + " x " + height);
                resize(width, height);
            }
        }
    }
}
