package com.example.pigfarmersim.managers;

import android.graphics.Canvas;
import java.util.*;

import android.graphics.PointF;
import android.util.ArraySet;

import com.example.pigfarmersim.entities.CustomerThread;
import com.example.pigfarmersim.entities.Table;
import com.example.pigfarmersim.environments.Floor;
import com.example.pigfarmersim.helpers.Bounds;
import com.example.pigfarmersim.helpers.GameConstants;


public class TableManager {
    public final Set<PointF> tablePool = new ArraySet<>();
    private final Object mutex = new Object();
    private boolean isFull = false;
    private final Map<String, Bounds> limits = new HashMap<>();

    public TableManager() {
        limits.put("downstairs", new Bounds(270f, 580f, 270f, 1536f));
        limits.put("upstairs", new Bounds(650f, 960f, 270f, 1536f));
        generateTableLayout();
    }

    private void generateTableLayout() {
        final int DOWNSTAIRS_ROWS = 3;
        final int UPSTAIRS_ROWS = 2;
        final int TOTAL_TABLES = GameConstants.TABLE_SLOTS;
        int cols = (int) Math.ceil((double) TOTAL_TABLES / (DOWNSTAIRS_ROWS + UPSTAIRS_ROWS));

        Bounds downstairs = limits.get("downstairs");
        if (downstairs == null) throw new RuntimeException("Invalid bounds for downstairs tables");

        float startX = downstairs.left * Floor.OUTSIDE.sx;
        float endX = downstairs.right * Floor.OUTSIDE.sx;
        float spacingX = (endX - startX - cols * 8 * Table.TABLE.spriteWidth) / (cols + 2);

        float startY = downstairs.top * Floor.OUTSIDE.sy;
        float endY = downstairs.bottom * Floor.OUTSIDE.sy;
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

        Bounds upstairs = limits.get("upstairs");
        if (upstairs == null) throw new RuntimeException("Invalid bounds for upstairs tables");

        startY = upstairs.top * Floor.OUTSIDE.sy;
        endY = upstairs.bottom * Floor.OUTSIDE.sy;
        spacingY = (endY - startY - UPSTAIRS_ROWS * 8 * Table.TABLE.spriteHeight) / (UPSTAIRS_ROWS + 2);

        y = startY + spacingY;
        for (int row = 0; row < UPSTAIRS_ROWS; row++) {
            float x = startX + spacingX;
            for (int col = 0; col < cols; col++) {
                tablePool.add(new PointF(x, y)); // ← save in world coordinates
                if (tablePool.size() == TOTAL_TABLES) return; // Needed: may reach total capacity here too
                x += 8 * Table.TABLE.spriteWidth + spacingX;
            }
            y += 8 * Table.TABLE.spriteHeight + spacingY;
        }
    }

    public void drawAll(Canvas c) {
        List<PointF> tablePoolCopy = new ArrayList<>(tablePool);

        for (PointF pos : tablePoolCopy) {
            c.drawBitmap(Table.TABLE.getSprite(), pos.x, pos.y, null);
        }
    }

    public void giveFreeTables(CustomerThread group) {
        synchronized (mutex) {
            List<PointF> freeTables = new ArrayList<>();
            Iterator<PointF> tableIter = tablePool.iterator();
            while (tableIter.hasNext() && freeTables.size() < group.groupSize) {
                freeTables.add(tableIter.next());
                tableIter.remove();
            }
            group.listPoints = freeTables;
        }
    }

    public void returnFreeTables(CustomerThread group) {
        group.listPoints.removeIf(pos -> pos == null || pos.x == 0 || pos.y == 0);

        synchronized (mutex) {
            tablePool.addAll(group.listPoints);
        }
    }

    public void signalFull() {
        isFull = true;
    }
    public boolean isFull() {
        if (isFull) { acknowledgeFull(); return true; }
        return false;
    }
    private void acknowledgeFull() {
        isFull = false;
    }
}
