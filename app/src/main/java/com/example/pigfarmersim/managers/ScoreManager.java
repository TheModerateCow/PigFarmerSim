package com.example.pigfarmersim.managers;

import com.example.pigfarmersim.helpers.GameConstants;

public class ScoreManager {
    private int score = GameConstants.SCORE.INITIAL;

    private final Object mutex = new Object();

    public void success(int groupSize) {
        synchronized (mutex) {
            score += GameConstants.SCORE.REWARD * groupSize;
        }
    }

    public void failure(int groupSize) {
        synchronized (mutex) {
            score -= GameConstants.SCORE.PENALTY * groupSize;
        }
    }

    public int getScore() { return score; }
}
