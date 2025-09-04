To compile:
gcc -o serial serial_linked_list.c
gcc -o mutex pthread_mutex_linked_list.c -lpthread
gcc -o rwlock pthread_rwlock_linked_list.c -lpthread

To run:
./serial <runs>
./mutex <num_threads> <runs>
./rwlock <num_threads> <runs>

Arguments:
<num_threads> : Number of threads for parallel programs (mutex/rwlock)
<runs>        : Number of times to repeat the experiment for statistical analysis (all programs)

Example:
./serial 1000
./mutex 4 1000
./rwlock 8 1000