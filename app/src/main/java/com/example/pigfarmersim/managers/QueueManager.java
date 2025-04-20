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
    public final Object mutex = new Object();
    private final Map<String, Bounds> limits = new HashMap<>();

    public QueueManager() {
        limits.put("outside", new Bounds(140f, 1024f, 0f, 240f));
        generateQueueLayout();
    }

    public void generateQueueLayout() {
        final int OUTSIDE_COLS = 2;
        final int OUTSIDE_GROUPS = GameConstants.QUEUE_SLOTS;

        Bounds outside = limits.get("outside");
        if (outside == null) throw new RuntimeException("Invalid bounds for queue positions");

        float startX = outside.left * Floor.OUTSIDE.sx;
        float endX = outside.right * Floor.OUTSIDE.sx;
        float startY = outside.top * Floor.OUTSIDE.sy;
        float endY = outside.bottom * Floor.OUTSIDE.sy;
        float spacingX = (endX - startX - OUTSIDE_COLS * 4 * GameConstants.Sprite.DEFAULT_SIZE) / (OUTSIDE_COLS + 2);
        int rows = (int) Math.ceil((double) OUTSIDE_GROUPS / OUTSIDE_COLS);
        float spacingY = (endY - startY - rows * 4 * GameConstants.Sprite.DEFAULT_SIZE) / (rows + 2);

        float y = startY + spacingY;
        for (int row = 0; row < rows; row++) {
            float x = startX + spacingX;
            for (int col = 0; col < OUTSIDE_COLS; col++) {
                queuePool.add(new PointF(x, y)); // ← save in world coordinates
                if (queuePool.size() == OUTSIDE_GROUPS) return;
                x += 4 * GameConstants.Sprite.DEFAULT_SIZE + spacingX;
            }
            y += 4 * GameConstants.Sprite.DEFAULT_SIZE + spacingY;
        }
    }

    public PointF giveFreeQueue() {
        if (queuePool.isEmpty()) {
            return null;
        }
        synchronized (mutex) {
            return queuePool.remove(0);
        }
    }

    public void returnFreeQueue(PointF pos) {
        if (pos == null || pos.x == 0 || pos.y == 0) return;
        synchronized (mutex) {
            queuePool.add(pos);
        }
    }
}
