package com.example.pigfarmersim;

import android.os.SystemClock;
import com.example.pigfarmersim.entities.CustomerThread;
import com.example.pigfarmersim.helpers.GameConstants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class CustomerSpawner implements Runnable{
    public final List<CustomerThread> customerPool = Collections.synchronizedList(new ArrayList<>());
    private boolean running = false;
    private final Random random = new Random();
    private long lastSpawnTime;
    private long nextSpawnDelay; // milliseconds

    public CustomerSpawner() {
        lastSpawnTime = SystemClock.elapsedRealtime();
        nextSpawnDelay = getRandomSpawnDelay();
    }

    private long getRandomSpawnDelay() {
        return 1000 + random.nextInt(GameConstants.GROUP_CONSTANTS.SPAWN_DELAY); // 1000ms to 5000ms (1s to 5s)
    }

    public List<CustomerThread> getCustomerGroups() {
        return customerPool;
    }

    private void addNewCustomerThread() {
        CustomerThread customer = new CustomerThread();
        customerPool.add(customer);
        new Thread(customer).start();
    }

    @Override
    public void run() {
        running = true;
        try{
            while (running) {
                long currentTime = SystemClock.elapsedRealtime();
                final int MAX_GROUPS = GameConstants.QUEUE_SLOTS;
                if (currentTime - lastSpawnTime >= nextSpawnDelay && customerPool.size() < MAX_GROUPS) {
                    addNewCustomerThread();
                    lastSpawnTime = SystemClock.elapsedRealtime();
                    nextSpawnDelay = getRandomSpawnDelay();
                }
            }
        } finally {
            for (CustomerThread customer: customerPool) {
                customer.interrupt();
            }
        }
    }

    public void interrupt() {
        running = false;
    }
}
