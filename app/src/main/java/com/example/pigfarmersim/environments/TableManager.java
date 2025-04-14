package com.example.pigfarmersim.environments;

import android.graphics.Canvas;
import java.util.ArrayList;
import java.util.List;

import android.graphics.Point;
import android.graphics.PointF;
import com.example.pigfarmersim.entities.Table;

public class TableManager {

    private class TableInstance {
        PointF pos = null;

        TableInstance(float worldX, float worldY) {
            pos = new PointF(worldX, worldY);
        }

        void draw(Canvas c, float cameraX, float cameraY) {
            // Draw based on world coordinates + camera offset (locked to world)
            c.drawBitmap(Table.TABLE.getSprite(), pos.x + cameraX, pos.y + cameraY, null);
        }
    }

    private List<TableInstance> tables;

    public TableManager() {
        tables = new ArrayList<>();
        generateTableLayout();
    }

    private void generateTableLayout() {
        float startX = 600f;
        float startY = 450f;
        float spacingX = 200f;
        float spacingY = 185f;

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 6; col++) {
                float x = startX + col * spacingX;
                float y = startY + row * spacingY;
                tables.add(new TableInstance(x, y)); // ← save in world coordinates
            }
        }
    }

    public List<PointF> getTablePoints() {
        List<PointF> ret = new ArrayList<>();
        for (TableInstance table : tables) {
            ret.add(table.pos);
        }
        return ret;
    }

    public void drawAll(Canvas canvas, float cameraX, float cameraY) {
        for (TableInstance table : tables) {
            table.draw(canvas, cameraX, cameraY);
        }
    }
}
