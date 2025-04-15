package com.example.pigfarmersim.environments;

import android.graphics.Canvas;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.graphics.PointF;
import com.example.pigfarmersim.entities.Table;
import com.example.pigfarmersim.helpers.Bounds;


public class TableManager {
    private final List<PointF> downstairTables = new ArrayList<>();
    private final List<PointF> upstairTables = new ArrayList<>();

    // Bounds class created bellow
    private final Map<String, Bounds> limits = new HashMap<>();

    private final int DOWNSTAIRS_ROWS = 3;
    private final int DOWNSTAIRS_TABLES = 30;
    private final int UPSTAIRS_ROWS = 2;
    private final int UPSTAIRS_TABLES = 10;

    public TableManager() {
        limits.put("downstairs", new Bounds(270f, 580f,270f,1536f));
        limits.put("upstairs", new Bounds(650f, 960f, 270f, 1536f));
        generateTableLayout();
    }

    private void generateTableLayout() {
        float startX = limits.get("downstairs").left * Floor.OUTSIDE.sx;
        float endX = limits.get("downstairs").right * Floor.OUTSIDE.sx;
        float startY = limits.get("downstairs").top * Floor.OUTSIDE.sy;
        float endY = limits.get("downstairs").bottom * Floor.OUTSIDE.sy;
        float spacingY = (endY - startY - DOWNSTAIRS_ROWS * 8 * Table.TABLE.spriteHeight) / (DOWNSTAIRS_ROWS + 2);
        int cols = (int) Math.ceil((double) DOWNSTAIRS_TABLES / DOWNSTAIRS_ROWS);
        float spacingX = (endX - startX - cols * 8 * Table.TABLE.spriteWidth) / (cols + 2);

        float y = startY + spacingY;
        for (int row = 0; row < DOWNSTAIRS_ROWS; row++) {
            float x = startX + spacingX;
            for (int col = 0; col < cols; col++) {
                downstairTables.add(new PointF(x, y)); // ← save in world coordinates
                x += 8 * Table.TABLE.spriteWidth + spacingX;
            }
            y += 8 * Table.TABLE.spriteHeight + spacingY;
        }

        startX = limits.get("upstairs").left * Floor.OUTSIDE.sx;
        endX = limits.get("upstairs").right * Floor.OUTSIDE.sx;
        startY = limits.get("upstairs").top * Floor.OUTSIDE.sy;
        endY = limits.get("upstairs").bottom * Floor.OUTSIDE.sy;
        spacingY = (endY - startY - UPSTAIRS_ROWS * 8 * Table.TABLE.spriteHeight) / (UPSTAIRS_ROWS + 2);
        cols = (int) Math.ceil((double) UPSTAIRS_TABLES / UPSTAIRS_ROWS);
        spacingX = (endX - startX - cols * 8 * Table.TABLE.spriteWidth) / (cols + 2);

        y = startY + spacingY;
        for (int row = 0; row < UPSTAIRS_ROWS; row++) {
            float x = startX + spacingX;
            for (int col = 0; col < cols; col++) {
                upstairTables.add(new PointF(x, y)); // ← save in world coordinates
                x += 8 * Table.TABLE.spriteWidth + spacingX;
            }
            y += 8 * Table.TABLE.spriteHeight + spacingY;
        }
    }

    public List<PointF> getDownstairTables() {
        return downstairTables;
    }
    public List<PointF> getUpstairTables() { return upstairTables; }

    public void drawAll(Canvas c) {
        for (PointF pos : downstairTables) {
            c.drawBitmap(Table.TABLE.getSprite(), pos.x, pos.y, null);
        }
        for (PointF pos : upstairTables) {
            c.drawBitmap(Table.TABLE.getSprite(), pos.x, pos.y, null);
        }
    }
}
