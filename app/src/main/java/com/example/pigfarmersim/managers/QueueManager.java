package com.example.pigfarmersim.managers;

import android.graphics.PointF;

import com.example.pigfarmersim.environments.Floor;
import com.example.pigfarmersim.helpers.Bounds;
import com.example.pigfarmersim.helpers.GameConstants;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QueueManager {
    public final List<PointF> queuePool = new ArrayList<>();
    private final Map<String, Bounds> limits = new HashMap<>();
    private final int OUTSIDE_COLS = 2;
    private final int OUTSIDE_GROUPS = GameConstants.QUEUE_SLOTS;

    public QueueManager() {
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
                queuePool.add(new PointF(x, y)); // ← save in world coordinates
                x += 4 * GameConstants.Sprite.DEFAULT_SIZE + spacingX;
            }
            y += 4 * GameConstants.Sprite.DEFAULT_SIZE + spacingY;
        }
    }

    public synchronized PointF giveFreeQueue() {
        if (queuePool.isEmpty()) {
            return null;
        }
        return queuePool.remove(0);
    }

    public synchronized void returnFreeQueue(PointF pos) {
        if (pos == null || pos.x == 0 || pos.y == 0) return;
        queuePool.add(pos);
    }
}
