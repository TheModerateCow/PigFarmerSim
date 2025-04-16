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
    public boolean jobDone = false;
    public boolean isComplete = false;
    private static final Random random = new Random();
    public PointF queuePoint = null;
    public List<PointF> listPoints = new ArrayList<>();
    // for timer
    private static final long WAITING_TIME = 20000; // 20 seconds
    private long waitingTimeLeft = WAITING_TIME;
    private long IOStartTime;
    private static final long JOB_TIME = 5000;
    private long jobTimeLeft = JOB_TIME;

    private long saveJobTimeLeft;
    private long spawnTime;
    private boolean waitingTimerRunning = false;
    private boolean jobTimerRunning = false;
    private boolean isOnIO = false;
    public int waitingTimerColor = Color.WHITE; // exposed for drawing
    public int jobTimerColor = Color.WHITE;

    public CustomerGroup() {
        this.groupSize = random.nextInt(4) + 1; // Random group size from 1 to 4
        for (int i = 0; i < this.groupSize; i++) {
            listPoints.add(new PointF());
        }
        this.spawnTime = System.currentTimeMillis();
    }

    public PointF getCurrent() {
        if (inQueue)
            return queuePoint;
        else
            return listPoints.get(0);
    }

    // to update the timer every MS
    public void updateTimer() {
        if (inQueue) {
            waitingTimeLeft -= System.currentTimeMillis() - spawnTime;
            spawnTime = System.currentTimeMillis();

            if (waitingTimeLeft < 0f) {
                waitingTimerRunning = false;
                waitingTimerColor = Color.BLACK; // Expired
                return;
            }

            float progress = (float) (WAITING_TIME - waitingTimeLeft) / WAITING_TIME;
            int red = 255;
            int greenBlue = (int) (255 * (1 - progress)); // fades from white -> red

            waitingTimerColor = Color.rgb(red, greenBlue, greenBlue);
        }
        else {
            long elapsed_time = System.currentTimeMillis() - spawnTime;
            this.jobTimeLeft = elapsed_time;
            if (elapsed_time >= JOB_TIME) {
                jobDone = true;
                isComplete = random.nextInt(100) > 50;
                return;
            }

            float progress = (float) elapsed_time / JOB_TIME;
            int green = 255;
            int redBlue = (int) (255 * (1 - progress)); // fades from white -> green

            jobTimerColor = Color.rgb(redBlue, green, redBlue);
        }
    }

    public long saveJobTimeLeft() {
        this.saveJobTimeLeft = this.jobTimeLeft;
        return this.saveJobTimeLeft;
    }

    public void reset() {
        inQueue = true;
        waitingTimeLeft = WAITING_TIME;
    }

    // draw the timer
    public void drawTimer(Canvas canvas, Paint paint) {
        PointF pos = getCurrent();
        if (pos == null)
            return; // Don't draw if position is not set

        paint.setTextSize(40);

        float timeLeftSec = 0;
        if (inQueue) {
            timeLeftSec = Math.max(0, waitingTimeLeft / 1000f);
            paint.setColor(waitingTimerColor);
        } else {
            timeLeftSec = Math.max(0, (JOB_TIME - (System.currentTimeMillis() - spawnTime)) / 1000f);
            paint.setColor(jobTimerColor);
        }

        canvas.drawText(String.format("%.1fs", timeLeftSec), pos.x, pos.y + 110, paint);
    }

    public void setSpawnTime(long lastSpawnTime) {
        this.spawnTime = lastSpawnTime;
    }

    public void setOnIOEvent(boolean onIOEvent)  {
        if (onIOEvent == true) {
            this.IOStartTime = System.currentTimeMillis();
        }
        this.isOnIO = onIOEvent;
    }

    public boolean isWaitingTimerExpired() {
        return !waitingTimerRunning && waitingTimerColor == Color.BLACK;
    }

    // Check if job timer completed (customer successfully served)
    public boolean isJobCompleted() {
        return !jobTimerRunning && jobTimerColor == Color.GREEN;
    }
}
