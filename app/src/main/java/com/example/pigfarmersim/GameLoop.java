package com.example.pigfarmersim;

import com.example.pigfarmersim.entities.CustomerThread;
import com.example.pigfarmersim.managers.CustomerManager;

import java.util.List;

public class GameLoop implements Runnable {
    private Thread gameThread;
    private final GamePanel gamePanel;
    public volatile boolean running = false; // Added running flag

    public GameLoop(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
        gameThread = new Thread(this);
    }

    @Override
    public void run() {
        long lastFPScheck = System.currentTimeMillis();
        int fps = 0;

        while (running) {
            gamePanel.update();
            gamePanel.render();

            fps++;

            long now = System.currentTimeMillis();
            if (now - lastFPScheck >= 1000) {
                // 1005
                System.out.println("FPS: " + fps + " " + System.currentTimeMillis());
                fps = 0;
                lastFPScheck += 1000;
            }
        }
    }

    public void startGameLoop() {
        if (!running) {
            running = true;
            gameThread = new Thread(this);
            gameThread.start();
        }
    }

    public void stopGameLoop() {
        running = false;
        // Wait for the thread to finish
        try {
            if (gameThread != null) gameThread.join();
        } catch (InterruptedException ignore) {
        }
    }
}
