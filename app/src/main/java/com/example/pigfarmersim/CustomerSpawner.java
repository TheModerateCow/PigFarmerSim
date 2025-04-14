package com.example.pigfarmersim;

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

        // Add to the list of active customers
        customers.add(newCustomer);

        // You can also pass this list to GamePanel to render or interact with it
    }

    public List<CustomerGroup> getCustomerGroups() {
        return customers;
    }
}
