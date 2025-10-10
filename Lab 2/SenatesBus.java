import java.util.Random;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Senate Bus Problem Solution
 * Problem: Riders wait for a bus at a bus stop. When a bus arrives, all waiting riders
 * board (up to capacity of 50). Anyone arriving during boarding must wait for the next bus.
 * The bus departs when all waiting riders have boarded or immediately if no one is waiting.
 */

class BusStop {
    private static final int BUS_CAPACITY = 50;
    
    // Shared state
    private int waitingRiders = 0;
    private int ridersOnBus = 0;
    
    // Synchronization primitives
    private ReentrantLock mutex = new ReentrantLock();
    private Semaphore bus = new Semaphore(0);          // Bus waits for riders to board
    private Semaphore boardQueue = new Semaphore(0);   // Controls rider boarding
    
    /**
     * Called by a rider when they arrive at the bus stop
     */
    public void riderArrives(int riderId) throws InterruptedException {
        mutex.lock();
        try {
            waitingRiders++;
            System.out.println("Rider " + riderId + " arrived. Waiting riders: " + waitingRiders);
        } finally {
            mutex.unlock();
        }
        
        // Wait for permission to board
        boardQueue.acquire();
        
        // Board the bus
        boardBus(riderId);
    }
    
    /**
     * Called by a rider when they board the bus
     */
    private void boardBus(int riderId) {
        mutex.lock();
        try {
            ridersOnBus++;
            System.out.println("  Rider " + riderId + " is boarding the bus. (" + ridersOnBus + " riders on bus)");
        } finally {
            mutex.unlock();
        }
        
        // Signal the bus that one more rider has boarded
        bus.release();
    }
    
    /**
     * Called by a bus when it arrives at the bus stop
     */
    public void busArrives(int busId) throws InterruptedException {
        mutex.lock();
        int ridersToBoard = 0;
        
        try {
            System.out.println("\n*** Bus " + busId + " arrived. Waiting riders: " + waitingRiders + " ***");
            
            if (waitingRiders == 0) {
                System.out.println("*** Bus " + busId + " departing immediately (no riders waiting) ***\n");
                return; // Depart immediately
            }
            
            // Determine how many riders can board
            ridersToBoard = Math.min(waitingRiders, BUS_CAPACITY);
            waitingRiders -= ridersToBoard;
            ridersOnBus = 0;
            
            System.out.println("*** Bus " + busId + " allowing " + ridersToBoard + " riders to board ***");
            
            // Allow riders to board
            for (int i = 0; i < ridersToBoard; i++) {
                boardQueue.release();
            }
        } finally {
            mutex.unlock();
        }
        
        // Wait for all riders to board
        for (int i = 0; i < ridersToBoard; i++) {
            bus.acquire();
        }
        
        // All riders have boarded, depart
        depart(busId, ridersToBoard);
    }
    
    /**
     * Called by a bus when it departs
     */
    private void depart(int busId, int riderCount) {
        System.out.println("*** Bus " + busId + " departing with " + riderCount + " riders ***\n");
    }
}

/**
 * Represents a bus that arrives at the bus stop
 */
class Bus implements Runnable {
    private static int busCounter = 0;
    private int busId;
    private BusStop busStop;
    
    public Bus(BusStop busStop) {
        this.busId = ++busCounter;
        this.busStop = busStop;
    }
    
    @Override
    public void run() {
        try {
            busStop.busArrives(busId);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Bus " + busId + " interrupted");
        }
    }
}

/**
 * Represents a rider that arrives at the bus stop
 */
class Rider implements Runnable {
    private static int riderCounter = 0;
    private int riderId;
    private BusStop busStop;
    
    public Rider(BusStop busStop) {
        this.riderId = ++riderCounter;
        this.busStop = busStop;
    }
    
    @Override
    public void run() {
        try {
            busStop.riderArrives(riderId);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Rider " + riderId + " interrupted");
        }
    }
}

/**
 * Main simulation class
 */
public class SenatesBus {
    private static final double BUS_MEAN_ARRIVAL_TIME = 20 * 60 * 1000;    // 20 minutes in milliseconds
    private static final double RIDER_MEAN_ARRIVAL_TIME = 30 * 1000;       // 30 seconds in milliseconds
    private static final int SIMULATION_DURATION = 2 * 60 * 60 * 1000;     // 2 hours in milliseconds
    
    /**
     * Generate exponentially distributed random variable
     * @param mean Mean of the distribution
     * @return Exponentially distributed random value
     */
    private static long getExponentialRandom(double mean) {
        Random random = new Random();
        return (long) (-mean * Math.log(1 - random.nextDouble()));
    }
    
    public static void main(String[] args) {
        System.out.println("=== Senate Bus Problem Simulation ===");
        System.out.println("Bus capacity: 50 riders");
        System.out.println("Mean bus arrival time: 20 minutes");
        System.out.println("Mean rider arrival time: 30 seconds");
        System.out.println("Simulation duration: 2 hours\n");
        
        BusStop busStop = new BusStop();
        long startTime = System.currentTimeMillis();
        
        // Thread for bus arrivals
        Thread busThread = new Thread(() -> {
            try {
                while (System.currentTimeMillis() - startTime < SIMULATION_DURATION) {
                    long waitTime = getExponentialRandom(BUS_MEAN_ARRIVAL_TIME);
                    Thread.sleep(waitTime);
                    
                    Bus bus = new Bus(busStop);
                    new Thread(bus).start();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        
        // Thread for rider arrivals
        Thread riderThread = new Thread(() -> {
            try {
                while (System.currentTimeMillis() - startTime < SIMULATION_DURATION) {
                    long waitTime = getExponentialRandom(RIDER_MEAN_ARRIVAL_TIME);
                    Thread.sleep(waitTime);
                    
                    Rider rider = new Rider(busStop);
                    new Thread(rider).start();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        
        // Start simulation
        busThread.start();
        riderThread.start();
        
        // Wait for simulation to complete
        try {
            busThread.join();
            riderThread.join();
            
            // Give some time for remaining operations to complete
            Thread.sleep(5000);
            
            System.out.println("\n=== Simulation Complete ===");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Simulation interrupted");
        }
    }
}
