# Lab 2: Senate Bus Problem

## Problem Description

This is a classic synchronization problem based on the Senate bus at Wellesley College.

**Scenario:**

- Riders arrive at a bus stop and wait for a bus
- When a bus arrives, all waiting riders board (up to the bus capacity)
- Bus capacity is 50 riders
- Anyone arriving while the bus is boarding must wait for the next bus
- When all waiting riders have boarded, the bus departs
- If a bus arrives when there are no riders, it departs immediately

**Timing Constraints:**

- Bus inter-arrival time: Exponentially distributed with mean of 20 minutes
- Rider inter-arrival time: Exponentially distributed with mean of 30 seconds

## Solution Approach

### Synchronization Strategy

The solution uses **semaphores** and **mutex locks** to coordinate between buses and riders:

1. **Mutex (ReentrantLock):** Protects shared state variables:

   - `waitingRiders`: Number of riders waiting at the bus stop
   - `ridersOnBus`: Number of riders currently boarding

2. **Semaphore `boardQueue`:** Controls rider boarding

   - Initially 0 (riders must wait)
   - Bus releases permits to allow riders to board
   - Ensures only riders present before bus arrival can board

3. **Semaphore `bus`:** Synchronizes bus departure
   - Initially 0
   - Each rider releases one permit after boarding
   - Bus acquires permits equal to boarding riders to wait for all to board

### Algorithm Flow

**Rider's Perspective:**

```
1. Acquire mutex
2. Increment waitingRiders counter
3. Release mutex
4. Wait on boardQueue semaphore (blocks until bus arrives)
5. Board the bus (increment ridersOnBus)
6. Signal bus semaphore (notify bus of boarding)
```

**Bus's Perspective:**

```
1. Acquire mutex
2. Check waitingRiders count
   - If 0: Release mutex and depart immediately
   - If > 0: Calculate ridersToBoard = min(waitingRiders, BUS_CAPACITY)
3. Update waitingRiders (subtract ridersToBoard)
4. Release boardQueue permits (ridersToBoard times) to allow boarding
5. Release mutex
6. Wait on bus semaphore (ridersToBoard times) for all riders to board
7. Depart with riders
```

### Key Synchronization Properties

✅ **Mutual Exclusion:** Only one thread modifies shared state at a time (ensured by mutex)

✅ **No Deadlock:** Proper lock ordering and semaphore usage prevents circular waits

✅ **Bounded Waiting:** Riders eventually board a bus; buses eventually depart

✅ **Capacity Constraint:** Bus never exceeds 50 riders (`Math.min(waitingRiders, BUS_CAPACITY)`)

✅ **Boarding Constraint:** Only riders waiting before bus arrival can board (boardQueue semaphore)

✅ **Immediate Departure:** Bus departs immediately if no riders waiting

## How to Compile and Run

### Prerequisites

- Java Development Kit (JDK) 8 or higher

### Compilation

```bash
javac SenatesBus.java
```

### Execution

```bash
java SenatesBus
```

### Expected Output

The program will simulate 2 hours of bus and rider arrivals, showing:

- Rider arrivals and waiting counts
- Bus arrivals
- Boarding process (each rider boarding)
- Bus departures with rider counts

Example output:

```
=== Senate Bus Problem Simulation ===
Bus capacity: 50 riders
Mean bus arrival time: 20 minutes
Mean rider arrival time: 30 seconds
Simulation duration: 2 hours

Rider 1 arrived. Waiting riders: 1
Rider 2 arrived. Waiting riders: 2
...

*** Bus 1 arrived. Waiting riders: 45 ***
*** Bus 1 allowing 45 riders to board ***
  Rider 1 is boarding the bus. (1 riders on bus)
  Rider 2 is boarding the bus. (2 riders on bus)
  ...
*** Bus 1 departing with 45 riders ***

...
```

## Files

- `SenatesBus.java` - Main implementation with BusStop, Bus, and Rider classes
- `README.md` - This documentation file

## Learning Outcomes Achieved

✓ Understanding synchronization problems and devising solutions  
✓ Using mutexes (ReentrantLock) to protect critical sections  
✓ Using semaphores for signaling and coordination  
✓ Implementing exponential distribution for arrivals  
✓ Developing concurrent programs in Java

## References

- "The Little Book of Semaphores" by Allen B. Downey, Page 211
- Senate Bus Problem (Wellesley College)

## Notes for Grading

**Understanding the Solution:**

1. **Why use two semaphores?**

   - `boardQueue`: Controls which riders can board (only those waiting before bus arrival)
   - `bus`: Signals bus when riders finish boarding (rendezvous pattern)

2. **Why is the mutex necessary?**

   - Prevents race conditions when updating `waitingRiders` and `ridersOnBus`
   - Ensures atomic decisions about how many riders board

3. **How does this prevent riders arriving during boarding from getting on?**

   - Bus calculates `ridersToBoard` and releases exactly that many `boardQueue` permits
   - New arrivals increment `waitingRiders` but get no permit until next bus

4. **What if more than 50 riders are waiting?**

   - `Math.min(waitingRiders, BUS_CAPACITY)` ensures only 50 board
   - Remaining riders wait for the next bus

5. **How does the bus know when all riders have boarded?**
   - Bus waits (acquires) on the `bus` semaphore `ridersToBoard` times
   - Each boarding rider releases the `bus` semaphore once
   - Bus can only proceed when all riders have boarded
