package com.example.pigfarmersim;

import com.example.pigfarmersim.entities.CustomerThread;

import java.util.List;

public class GameLoop implements Runnable {
    private Thread gameThread;
    private GamePanel gamePanel;
    public volatile boolean running = false; // Added running flag

    private CustomerSpawner customerSpawner;

    public List<CustomerThread> customers;


    public GameLoop(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
        gameThread = new Thread(this);
    }

    @Override
    public void run() {
//        customerPool = customerSpawner.getCustomerGroups();
        long lastFPScheck = System.currentTimeMillis();
        int fps = 0;

        long lastDelta = System.nanoTime();
        long nanoSec = 1_000_000_000;

        while (running) {

            long nowDelta = System.nanoTime();
            double timeSinceLastDelta = nowDelta - lastDelta;
            double delta = timeSinceLastDelta / nanoSec;

            gamePanel.update(delta);
            gamePanel.render();
            lastDelta = nowDelta;

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
        running = false;  // Assuming you have a 'running' boolean flag
        // Wait for the thread to finish
        try {
            if (gameThread != null) {
                gameThread.join();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
