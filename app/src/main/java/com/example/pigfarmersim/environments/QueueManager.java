package com.example.pigfarmersim.environments;

import android.graphics.Canvas;
import android.graphics.PointF;

import com.example.pigfarmersim.entities.CustomerGroup;
import com.example.pigfarmersim.entities.Table;
import com.example.pigfarmersim.helpers.Bounds;
import com.example.pigfarmersim.helpers.GameConstants;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class QueueManager {
    private final List<PointF> freeQueues = new ArrayList<>();
    private List<PointF> freeDownstairTables;
    private List<PointF> upstairTables;
    private List<PointF> freeUpstairTables;
    private List<PointF> downstairTables;
    private final Map<String, Bounds> limits = new HashMap<>();

    private final int OUTSIDE_COLS = 2;
    private final int OUTSIDE_GROUPS = 6;

    public QueueManager(TableManager tableManager) {
        freeDownstairTables = tableManager.getDownstairTables();
        downstairTables = List.copyOf(freeDownstairTables);
        freeUpstairTables = tableManager.getUpstairTables();
        upstairTables = List.copyOf(freeUpstairTables);
        limits.put("outside", new Bounds(140f, 1024f, 0f, 240f));
        generateQueueLayout();
    }

    public void generateQueueLayout() {
        float startX = limits.get("outside").left * Floor.OUTSIDE.sx;
        float endX = limits.get("outside").right * Floor.OUTSIDE.sx;
        float startY = limits.get("outside").top * Floor.OUTSIDE.sy;
        float endY = limits.get("outside").bottom * Floor.OUTSIDE.sy;
        float spacingX = (endX - startX - OUTSIDE_COLS * 4 * GameConstants.Sprite.DEFAULT_SIZE) / (OUTSIDE_COLS + 2);
        int rows = (int) Math.ceil((double) OUTSIDE_GROUPS / OUTSIDE_COLS);
        float spacingY = (endY - startY - rows * 4 * GameConstants.Sprite.DEFAULT_SIZE) / (rows + 2);

        float y = startY + spacingY;
        for (int row = 0; row < rows; row++) {
            float x = startX + spacingX;
            for (int col = 0; col < OUTSIDE_COLS; col++) {
                freeQueues.add(new PointF(x, y)); // ← save in world coordinates
                x += 4 * GameConstants.Sprite.DEFAULT_SIZE + spacingX;
            }
            y += 4 * GameConstants.Sprite.DEFAULT_SIZE + spacingY;
        }
    }

    public void drawAll(Canvas c) {
        for (PointF pos : freeQueues) {
            c.drawBitmap(Table.TABLE.getSprite(), pos.x, pos.y, null);
        }
    }

    public PointF giveFreeQueue() {
        if (freeQueues.isEmpty()) {
            return null;
        }
        return freeQueues.remove(0);
    }

    public void returnFreeQueue(PointF point) {
        freeQueues.add(point);
    }

    public void giveFreeTables(CustomerGroup group) {
        List<PointF> freeTables = new ArrayList<>();
        Iterator<PointF> tableIter = freeDownstairTables.iterator();
        while (tableIter.hasNext() && freeTables.size() < group.groupSize) {
            freeTables.add(tableIter.next());
            tableIter.remove();
        }

        // JIC downstairs not enough tables
        tableIter = freeUpstairTables.iterator();
        while (tableIter.hasNext() && freeTables.size() < group.groupSize) {
            freeTables.add(tableIter.next());
            tableIter.remove();
        }
        group.listPoints = freeTables;
    }

    public void returnFreeTables(CustomerGroup group) {
        for (PointF pos : group.listPoints) {
            if (upstairTables.contains(pos)) freeUpstairTables.add(pos);
            else freeDownstairTables.add(pos);
        }
    }

    public int numberOfFreeTables() {
        return freeDownstairTables.size() + freeUpstairTables.size();
    }
}
