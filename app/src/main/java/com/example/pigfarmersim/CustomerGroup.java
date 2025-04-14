package com.example.pigfarmersim;

import java.util.Random;

public class CustomerGroup {

    private int groupSize; // 1 to 4
    private static final Random random = new Random();

    public CustomerGroup() {
        this.groupSize = random.nextInt(4) + 1; // Random group size from 1 to 4
    }

    public CustomerGroup(int groupSize) {
        this.groupSize = groupSize;
    }

    public int getGroupSize() {
        return groupSize;
    }

    public void setGroupSize(int groupSize) {
        if (groupSize >= 1 && groupSize <= 4) {
            this.groupSize = groupSize;
        } else {
            throw new IllegalArgumentException("Group size must be between 1 and 4.");
        }
    }

    // Add any other methods like getSprite(...) etc. as needed
}
