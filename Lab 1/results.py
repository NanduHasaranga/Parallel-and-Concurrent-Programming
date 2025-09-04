import matplotlib.pyplot as plt

threads = [1, 2, 4, 8]
serial = [23.600, 23.600, 23.600, 23.600]
mutex = [24.040, 26.502, 23.352, 20.154]
rwlock = [23.930, 9.160, 4.180, 2.020]

plt.plot(threads, serial, label='Serial')
plt.plot(threads, mutex, label='Mutex')
plt.plot(threads, rwlock, label='Read-Write Lock')
plt.xlabel('Number of Threads')
plt.ylabel('Average Execution Time (ms)')
plt.title('Execution Time vs Threads (Case 3)')
plt.legend()
plt.show()

# Additional graph: Speedup
speedup_mutex = [serial[0]/m for m in mutex]
speedup_rwlock = [serial[0]/r for r in rwlock]

plt.figure(figsize=(10,5))
plt.subplot(1,2,1)
plt.plot(threads, serial, label='Serial')
plt.plot(threads, mutex, label='Mutex')
plt.plot(threads, rwlock, label='Read-Write Lock')
plt.xlabel('Number of Threads')
plt.ylabel('Avg Execution Time (ms)')
plt.title('Execution Time vs Threads (Case 3)')
plt.legend()

plt.subplot(1,2,2)
plt.plot(threads, speedup_mutex, label='Mutex Speedup')
plt.plot(threads, speedup_rwlock, label='RWLock Speedup')
plt.xlabel('Number of Threads')
plt.ylabel('Speedup (Serial Time / Parallel Time)')
plt.title('Speedup vs Threads (Case 3)')
plt.legend()

plt.tight_layout()
plt.show()

