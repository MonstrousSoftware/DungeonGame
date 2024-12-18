package com.monstrous.dungeongame;

import com.badlogic.gdx.utils.Array;

public class MessageBox {
    public static Array<String> lines;

    public MessageBox() {
        lines = new Array<>();
        clear();
    }

    public static void addLine(String message){
        lines.add(message);
    }

    public static void clear(){
        lines.clear();
        for(int i = 0; i < 10; i++)
            addLine("");
    }
}
