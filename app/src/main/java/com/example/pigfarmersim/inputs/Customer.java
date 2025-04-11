package com.example.pigfarmersim.inputs;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

// Customer (Process)
class Customer {
    public int customerId;
    public String order; // e.g., "Print Document", "Save File", "Send Email"
    public CustomerStatus status;
    public int waitTime;
    public Location location;
    public String device;

    public Customer(int customerId, String order) {
        this.customerId = customerId;
        this.order = order;
        this.status = CustomerStatus.INSIDE;
        this.waitTime = 0;
        this.location = new Location(0,0);
        this.device = "";
    }
}

enum CustomerStatus {
    INSIDE, WAITING, PROCESSING, DONE, OUTSIDE
}

// I/O Device
class IODevice {
    public int deviceId;
    public String deviceType; // e.g., "Printer", "Disk", "Network"
    public DeviceStatus status;
    public int processingTime; // Processing time in seconds

    public IODevice(int deviceId, String deviceType) {
        this.deviceId = deviceId;
        this.deviceType = deviceType;
        this.status = DeviceStatus.IDLE;
        Random random = new Random();
        this.processingTime = random.nextInt(4) + 2; // Random time between 2 and 5
    }
}

enum DeviceStatus {
    IDLE, BUSY
}

class Location {
    public int x;
    public int y;

    public Location(int x, int y) {
        this.x = x;
        this.y = y;
    }
}

class Restaurant {
    public List<Customer> customers;
    public List<IODevice> ioDevices;
    private int nextCustomerId = 1;
    private int nextDeviceId = 1;

    public Restaurant() {
        customers = new ArrayList<>();
        ioDevices = new ArrayList<>();
    }

    // Adding logic
    public void addCustomer(String order){
        Customer customer = new Customer(nextCustomerId++, order);
        customers.add(customer);
    }

    public void addDevice(String deviceType){
        IODevice ioDevice = new IODevice(nextDeviceId++, deviceType);
        ioDevices.add(ioDevice);
    }

    public void processCustomers() {
        new Thread(() -> {
            while (true) {
                try {
                    // Check and process
                    List<Customer> waitingCustomers = new ArrayList<>();
                    for (Customer customer : customers) {
                        if (customer.status == CustomerStatus.WAITING) {
                            waitingCustomers.add(customer);
                        }
                    }

                    List<IODevice> idleDevices = new ArrayList<>();
                    for (IODevice device : ioDevices) {
                        if (device.status == DeviceStatus.IDLE) {
                            idleDevices.add(device);
                        }
                    }

                    // Check first if there are customers waiting
                    if (!waitingCustomers.isEmpty() && !idleDevices.isEmpty()) {
                        IODevice device = idleDevices.get(new Random().nextInt(idleDevices.size()));
                        Customer customer = waitingCustomers.get(new Random().nextInt(waitingCustomers.size()));
                        // If they are waiting, we must move them inside
                        moveInside(customer);
                        assignDevice(customer, device);
                    }

                    // Increase all customers who are waiting
                    for (Customer customer : customers) {
                        if (customer.status == CustomerStatus.WAITING) {
                            customer.waitTime++;
                        }
                    }

                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    // Method to assign a device to a customer
    private void assignDevice(Customer customer, IODevice device) {
        customer.status = CustomerStatus.PROCESSING;
        customer.device = device.deviceType;
        device.status = DeviceStatus.BUSY;
        // We will set a delay so the processing time could be simulated
        ioDeviceProcessing(device, customer);
    }

    private void moveOutside(Customer customer) {
        customer.location = new Location(0, 0); // We assume that 0,0 is outside
        customer.status = CustomerStatus.OUTSIDE;
    }

    private void moveInside(Customer customer) {
        customer.location = new Location(10, 10); // We assume that 10,10 is inside
        customer.status = CustomerStatus.INSIDE;
    }

    private void ioDeviceProcessing(IODevice device, Customer customer) {
        // Simulate processing time
        new Thread(() -> {
            try {
                Thread.sleep(device.processingTime * 1000); // Simulate time in seconds
                customerProcessed(customer, device);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void customerProcessed(Customer customer, IODevice device) {
        // Now we can set customer as done
        customer.status = CustomerStatus.DONE;
        // Also, the device is free
        device.status = DeviceStatus.IDLE;
    }

    // Simulates a customer needing a device
    public void customerNeedsDevice(Customer customer) {
        // We check if the device the customer needs is busy
        IODevice device = null;
        for (IODevice ioDevice : ioDevices) {
            if (ioDevice.deviceType.equals(customer.device)) {
                device = ioDevice;
                break;
            }
        }

        if (device == null || device.status == DeviceStatus.BUSY) {
            // We will move this customer outside
            moveOutside(customer);
            customer.status = CustomerStatus.WAITING;
        } else {
            // Device is free
            assignDevice(customer, device);
        }
    }
}