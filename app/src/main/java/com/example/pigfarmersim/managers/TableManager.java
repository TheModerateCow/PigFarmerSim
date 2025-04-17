package com.example.pigfarmersim.managers;

import android.graphics.Canvas;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.graphics.PointF;

import com.example.pigfarmersim.entities.CustomerThread;
import com.example.pigfarmersim.entities.Table;
import com.example.pigfarmersim.environments.Floor;
import com.example.pigfarmersim.helpers.Bounds;
import com.example.pigfarmersim.helpers.GameConstants;


public class TableManager {
    private final List<PointF> tablePool = new ArrayList<>();
    private final Map<String, Bounds> limits = new HashMap<>();
    private final int DOWNSTAIRS_ROWS = 3;
    private final int UPSTAIRS_ROWS = 2;
    private final int TOTAL_TABLES = GameConstants.TABLE_SLOTS;

    public TableManager() {
        limits.put("downstairs", new Bounds(270f, 580f, 270f, 1536f));
        limits.put("upstairs", new Bounds(650f, 960f, 270f, 1536f));
        generateTableLayout();
    }

    private void generateTableLayout() {
        int cols = (int) Math.ceil((double) TOTAL_TABLES / (DOWNSTAIRS_ROWS + UPSTAIRS_ROWS));
        float startX = limits.get("downstairs").left * Floor.OUTSIDE.sx;
        float endX = limits.get("downstairs").right * Floor.OUTSIDE.sx;
        float spacingX = (endX - startX - cols * 8 * Table.TABLE.spriteWidth) / (cols + 2);

        float startY = limits.get("downstairs").top * Floor.OUTSIDE.sy;
        float endY = limits.get("downstairs").bottom * Floor.OUTSIDE.sy;
        float spacingY = (endY - startY - DOWNSTAIRS_ROWS * 8 * Table.TABLE.spriteHeight) / (DOWNSTAIRS_ROWS + 2);

        float y = startY + spacingY;
        for (int row = 0; row < DOWNSTAIRS_ROWS; row++) {
            float x = startX + spacingX;
            for (int col = 0; col < cols; col++) {
                tablePool.add(new PointF(x, y)); // ← save in world coordinates
                x += 8 * Table.TABLE.spriteWidth + spacingX;
            }
            y += 8 * Table.TABLE.spriteHeight + spacingY;
        }

        startY = limits.get("upstairs").top * Floor.OUTSIDE.sy;
        endY = limits.get("upstairs").bottom * Floor.OUTSIDE.sy;
        spacingY = (endY - startY - UPSTAIRS_ROWS * 8 * Table.TABLE.spriteHeight) / (UPSTAIRS_ROWS + 2);

        y = startY + spacingY;
        for (int row = 0; row < UPSTAIRS_ROWS; row++) {
            float x = startX + spacingX;
            for (int col = 0; col < cols; col++) {
                tablePool.add(new PointF(x, y)); // ← save in world coordinates
                x += 8 * Table.TABLE.spriteWidth + spacingX;
            }
            y += 8 * Table.TABLE.spriteHeight + spacingY;
        }
    }

    public synchronized void drawAll(Canvas c) {
        List<PointF> tablePoolCopy = new ArrayList<>(tablePool);

        for (PointF pos : tablePoolCopy) {
            c.drawBitmap(Table.TABLE.getSprite(), pos.x, pos.y, null);
        }
    }

    public synchronized void giveFreeTables(CustomerThread group) {
        if (tablePool.size() < group.groupSize) {
            return;
        }

        List<PointF> freeTables = new ArrayList<>();
        Iterator<PointF> tableIter = tablePool.iterator();
        while (tableIter.hasNext() && freeTables.size() < group.groupSize) {
            freeTables.add(tableIter.next());
            tableIter.remove();
        }

        group.listPoints = freeTables;
    }

    public synchronized void returnFreeTables(CustomerThread group) {
        for (PointF pos : group.listPoints) { tablePool.add(pos); }
    }
}
