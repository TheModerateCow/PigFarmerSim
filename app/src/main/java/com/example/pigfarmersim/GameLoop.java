package com.example.pigfarmersim;

/**
 * The GameLoop class is responsible for running the game loop.
 * It updates the game state and renders the game.
 */
public class GameLoop implements Runnable {
    /** The thread for the game loop */
    private Thread gameThread;
    /** The game panel */
    private GamePanel gamePanel;

    /**
     * Constructor for the GameLoop class.
     * 
     * @param gamePanel the game panel to run the game loop on.
     */
    public GameLoop(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
        gameThread = new Thread(this);
    }

    /**
     * The run method for the game loop.
     */
    @Override
    public void run() {
        /** The last time the FPS was checked */
        long lastFPScheck = System.currentTimeMillis();

        int fps = 0;

        /** The last time the delta was checked */
        long lastDelta = System.nanoTime();

        /** The nano seconds */
        long nanoSec = 1_000_000_000;

        while (true) {
            /** The current time the delta was checked */
            long nowDelta = System.nanoTime();

            /** The time since the last delta was checked */
            double timeSinceLastDelta = nowDelta - lastDelta;

            /** The delta */
            double delta = timeSinceLastDelta / nanoSec;

            /** Updates the game state */
            gamePanel.update(delta);

            /** Renders the game */
            gamePanel.render();

            /** Updates the last delta */
            lastDelta = nowDelta;

            /** Increments and counts the the FPS within 1 second */
            fps++;

            /** The current time */
            long now = System.currentTimeMillis();
            /** Constantly checks the FPS after every 1 second */
            if (now - lastFPScheck >= 1000) {
                System.out.println("FPS: " + fps + " " + System.currentTimeMillis());
                /** Resets the FPS */
                fps = 0;
                /** Updates the last FPS check time */
                lastFPScheck += 1000;
            }
        }
    }

    /**
     * Starts the game loop.
     */
    public void startGameLoop() {
        gameThread.start();
    }
}
