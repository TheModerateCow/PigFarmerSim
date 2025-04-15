package com.example.pigfarmersim.environments;

import android.graphics.Canvas;

import com.example.pigfarmersim.MainActivity;
import com.example.pigfarmersim.helpers.GameConstants;

public class MapManager {
    public void draw(Canvas c) {
        c.drawBitmap(Floor.OUTSIDE.getBackground(), 0, 0, null);
    }
}
