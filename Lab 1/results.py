# Activate your virtual environment for the project (e.g., `venv\Scripts\activate` on Windows)
# Then install matplotlib locally using:
# pip install matplotlib

import matplotlib.pyplot as plt

threads = [1, 2, 4, 8]
serial = [0.008000, 0.008000, 0.008000, 0.008000]
mutex = [0.008000, 0.014000, 0.013000, 0.015000]
rwlock = [0.008000, 0.005000, 0.004000, 0.003000]

plt.plot(threads, serial, label='Serial')
plt.plot(threads, mutex, label='Mutex')
plt.plot(threads, rwlock, label='Read-Write Lock')
plt.xlabel('Number of Threads')
plt.ylabel('Average Execution Time (ms)')
plt.title('Execution Time vs Threads')
plt.legend()
plt.show()