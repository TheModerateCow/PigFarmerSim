package com.example.pigfarmersim.entities;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;

import com.example.pigfarmersim.helpers.GameConstants;
import com.example.pigfarmersim.managers.ScoreManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CustomerThread implements Runnable {
    private Thread thread = null;
    private final ScoreManager scoreManager;
    private static final Random random = new Random();
    public int groupSize = random.nextInt(GameConstants.GROUP_CONSTANTS.MAX_SIZE) + 1; // Random group size from 1 to 11
    private boolean running = false;
    public boolean inQueue = true;
    public boolean jobDone = false;
    public boolean waitExpire = false;
    public PointF queuePoint = null;
    public List<PointF> listPoints = new ArrayList<>();
    // for timer
    private long waitingTimeLeft;
    private final long waitingTime = GameConstants.CUSTOMER_THREAD_CONSTANTS.WAITING_TIME + random.nextInt(10000);
//            (long) ((GameConstants.CUSTOMER_THREAD_CONSTANTS.WAITING_TIME) * random.nextFloat());
    private long spawnTime;
    public int waitingTimerColor = Color.WHITE; // exposed for drawing
    public int jobTimerColor = Color.WHITE;

    public CustomerThread(ScoreManager scoreManager) {
        this.scoreManager = scoreManager;
        for (int i = 0; i < this.groupSize; i++) {
            listPoints.add(new PointF());
        }
        this.spawnTime = System.currentTimeMillis();
        this.waitingTimeLeft = waitingTime;
    }

    public PointF getCurrent() {
        if (inQueue)
            return queuePoint;
        else
            return listPoints.get(0);
    }

    // draw the timer
    public void drawTimer(Canvas canvas, Paint paint) {
        PointF pos = getCurrent();
        if (pos == null) return; // Don't draw if position is not set

        paint.setTextSize(40);

        float timeLeftSec;
        if (inQueue) {
            timeLeftSec = Math.max(0, waitingTimeLeft / 1000f);
            paint.setColor(waitingTimerColor);
        } else {
            timeLeftSec = Math.max(0, (GameConstants.CUSTOMER_THREAD_CONSTANTS.JOB_TIME - (System.currentTimeMillis() - spawnTime)) / 1000f);
            paint.setColor(jobTimerColor);
        }
        canvas.drawText(String.format("%.1fs", timeLeftSec), pos.x, pos.y + 110, paint);

    }

    @Override
    public void run() {
        while (running && (!waitExpire || !jobDone) ) {
            if (inQueue) {
                waitingTimeLeft -= System.currentTimeMillis() - spawnTime;
                spawnTime = System.currentTimeMillis();

                if (waitingTimeLeft < 0f) {
                    scoreManager.failure(groupSize);
                    waitExpire = true;
                    return;
                }

                float progress = (float) (waitingTime - waitingTimeLeft) / waitingTime;
                int red = 255;
                int greenBlue = (int) (255 * (1 - progress)); // fades from white -> red

                waitingTimerColor = Color.rgb(red, greenBlue, greenBlue);
            }
            else {
                long elapsed_time = System.currentTimeMillis() - spawnTime;
                if (elapsed_time >= GameConstants.CUSTOMER_THREAD_CONSTANTS.JOB_TIME) {
                    scoreManager.success(groupSize);
                    jobDone = true;
                    return;
                }

                float progress = (float) elapsed_time / GameConstants.CUSTOMER_THREAD_CONSTANTS.JOB_TIME;
                int green = 255;
                int redBlue = (int) (255 * (1 - progress)); // fades from white -> green

                jobTimerColor = Color.rgb(redBlue, green, redBlue);
            }
        }
    }
    public void startThread() {
        running = true;
        thread = new Thread(this);
        thread.start();
    }

    public void stopThread() {
        running = false;
        try {
            if (thread != null) thread.join();
        } catch (InterruptedException ignore){
        }
    }
}
