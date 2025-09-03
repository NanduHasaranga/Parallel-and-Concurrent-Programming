To compile:
gcc -o serial serial_linked_list.c
gcc -o mutex pthread_mutex_linked_list.c -lpthread
gcc -o rwlock pthread_rwlock_linked_list.c -lpthread

To run:
./serial
./mutex <num_threads>
./rwlock <num_threads>