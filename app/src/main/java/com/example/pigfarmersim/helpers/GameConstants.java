package com.example.pigfarmersim.helpers;

public final class GameConstants {
    public static final int TABLE_SLOTS = 45;
    public static final int QUEUE_SLOTS = 6;
    public static final class SCORE {
        public static final int INITIAL = 0;
        public static final int PENALTY = 10;
        public static final int REWARD = 20;
    }
    public static final class GROUP_CONSTANTS {
        public static final int MAX_SIZE = 10;
        public static final int SPAWN_DELAY = 4000;
    }
    public static final class Face_Dir{
        public static final int DOWN = 0;
        public static final int UP = 1;
        public static final int LEFT = 2;
        public static final int RIGHT = 3;
    }

    public static final class Sprite{
        public static final int DEFAULT_SIZE = 16;
        public static final int SCALE_MULTIPLIER = 1;
        public static final int SIZE = DEFAULT_SIZE * SCALE_MULTIPLIER;
    }

    public static final class CUSTOMER_THREAD_CONSTANTS {
        public static final long WAITING_TIME = 10000; // 10 second (range from 10 to 20)
        public static final long JOB_TIME = 5000;
    }

    public static final class GAME_PANEL_CONSTANTS {
        public static final int MAX_PROCESSES = 5;
    }

    public static final class SCORE_FLASHING {
        public static final long FLASH_DURATION = 1000; // total duration of flashing (e.g., 1s)
        public static final long FLASH_INTERVAL = 200; // how often it blinks
    }
}
