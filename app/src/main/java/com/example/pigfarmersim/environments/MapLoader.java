package com.example.pigfarmersim.environments;

import android.graphics.Canvas;

public class MapLoader {
    public void draw(Canvas c) {
        c.drawBitmap(Floor.OUTSIDE.getBackground(), 0, 0, null);
    }
}
