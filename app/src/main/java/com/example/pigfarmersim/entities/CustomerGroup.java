package com.example.pigfarmersim.entities;

import android.graphics.PointF;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class CustomerGroup {

    public int groupSize; // 1 to 4
    public boolean inQueue = true;
    private static final Random random = new Random();
    public PointF queuePoint = null;
    public List<PointF> listPoints = new ArrayList<>();

    public CustomerGroup() {
        this.groupSize = random.nextInt(4) + 1; // Random group size from 1 to 4
        for (int i = 0; i < this.groupSize; i++) {
            listPoints.add(new PointF());
        }
    }

    public PointF getCurrent() {
        if (inQueue) return queuePoint;
        else return listPoints.get(0);
    }
}
