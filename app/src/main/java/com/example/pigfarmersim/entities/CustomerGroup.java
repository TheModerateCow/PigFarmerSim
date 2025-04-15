package com.example.pigfarmersim.entities;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CustomerGroup {

    public int groupSize; // 1 to 4
    public boolean inQueue = true;
    private static final Random random = new Random();
    public PointF queuePoint = null;
    public List<PointF> listPoints = new ArrayList<>();

    // for timer
    private static final long WAITING_TIME = 20000; // 20 seconds
    private static final long JOB_TIME = 5000;
    private long spawnTime;
    private boolean waitingTimerRunning = false;
    private boolean jobTimerRunning = false;
    public int waitingTimerColor = Color.WHITE; // exposed for drawing
    public int jobTimerColor = Color.WHITE;

    public CustomerGroup() {
        this.groupSize = random.nextInt(4) + 1; // Random group size from 1 to 4
        for (int i = 0; i < this.groupSize; i++) {
            listPoints.add(new PointF());
        }
        this.startWaitingTimer();
    }

    public PointF getCurrent() {
        if (inQueue) return queuePoint;
        else return listPoints.get(0);
    }

    // start timer
    public void startWaitingTimer() {
        spawnTime = System.currentTimeMillis();
        waitingTimerRunning = true;
    }

    public void startJobTimer() {
        spawnTime = System.currentTimeMillis();
        jobTimerRunning = true;
    }

    // to update the timer every MS
    public void updateTimer() {
        if (!waitingTimerRunning && inQueue) return;

        else if (inQueue) {
            long elapsedTime = System.currentTimeMillis() - spawnTime;

            if (elapsedTime >= WAITING_TIME) {
                waitingTimerRunning = false;
                waitingTimerColor = Color.BLACK; // Expired
                return;
            }

            float progress = (float) elapsedTime / WAITING_TIME;
            int red = 255;
            int greenBlue = (int) (255 * (1 - progress)); // fades from white -> red

            waitingTimerColor = Color.rgb(red, greenBlue, greenBlue);
        }

        else {
            long elapsedTime = System.currentTimeMillis() - spawnTime;
            if (elapsedTime >= JOB_TIME) {
                jobTimerRunning = false;
                jobTimerColor = Color.GREEN; // Expired
                return;
            }

            float progress = (float) elapsedTime / JOB_TIME;
            int green = 255;
            int redBlue = (int) (255 * (1 - progress)); // fades from white -> red

            jobTimerColor = Color.rgb(redBlue, green, redBlue);
        }
    }

    public void setStartTime(long spawnTime) {
        this.spawnTime = spawnTime;
    }

    // draw the timer
    public void drawTimer(Canvas canvas, Paint paint) {
        PointF pos = getCurrent();
        if (pos == null) return; // Don't draw if position is not set

        paint.setColor(waitingTimerColor);
        paint.setTextSize(40);

        float timeLeftSec = 0;
        if (waitingTimerRunning) {
            timeLeftSec = Math.max(0, (WAITING_TIME - (System.currentTimeMillis() - spawnTime)) / 1000f);
        }

        if (jobTimerRunning) {
            timeLeftSec = Math.max(0, (JOB_TIME - (System.currentTimeMillis() - spawnTime)) / 1000f);
        }

        canvas.drawText(String.format("%.1fs", timeLeftSec), pos.x, pos.y + 110, paint);
    }

    public void stopWaitingTimer() {
        waitingTimerRunning = false;
//        WAITING_TIME = System.currentTimeMillis() - spawnTime;
    }

    public void stopJobTimer() {
        jobTimerRunning = false;
//        JOB_TIME = System.currentTimeMillis() - spawnTime;
    }
}
