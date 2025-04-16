package com.example.pigfarmersim;

import android.graphics.PointF;
import android.os.SystemClock;

import com.example.pigfarmersim.entities.CustomerGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CustomerSpawner {
    public final List<CustomerGroup> customers = new ArrayList<>();
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
            lastSpawnTime = currentTime;
            spawnCustomer(lastSpawnTime);
            nextSpawnDelay = getRandomSpawnDelay();
        }
    }

    private void spawnCustomer(long lastSpawnTime) {
        CustomerGroup customer = new CustomerGroup();
        customer.setSpawnTime(lastSpawnTime);
        customers.add(new CustomerGroup());
    }

    public List<CustomerGroup> getCustomerGroups() {
        return customers;
    }
}
