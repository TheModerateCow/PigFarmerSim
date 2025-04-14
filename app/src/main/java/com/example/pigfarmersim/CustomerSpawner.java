package com.example.pigfarmersim;

import android.graphics.PointF;
import android.os.SystemClock;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CustomerSpawner {
    private final List<CustomerGroup> customers = new ArrayList<>();
    private final Random random = new Random();
    private long lastSpawnTime;
    private long nextSpawnDelay; // milliseconds

    public CustomerSpawner() {
        lastSpawnTime = SystemClock.elapsedRealtime();
        nextSpawnDelay = getRandomSpawnDelay();
    }

    private long getRandomSpawnDelay() {
        return 1000 + random.nextInt(4000); // 1000ms to 5000ms (1s to 5s)
    }

    public void update() {
        long currentTime = SystemClock.elapsedRealtime();
        if (currentTime - lastSpawnTime >= nextSpawnDelay && customers.size() < 5) {
            spawnCustomer();
            lastSpawnTime = currentTime;
            nextSpawnDelay = getRandomSpawnDelay();
        }
    }

    private void spawnCustomer() {
        // Create a new customer instance with desired parameters
        CustomerGroup newCustomer = new CustomerGroup();

        // Assign position in the queue
        float baseX = 100; // Starting X position
        float baseY = 100; // Y position for the queue
        float spacing = 160; // Space between groups

        float x = baseX + customers.size() * spacing;
        float y = baseY;

        newCustomer.setCoords(new PointF(x, y));

        customers.add(newCustomer);
    }

    public List<CustomerGroup> getCustomerGroups() {
        return customers;
    }
}
