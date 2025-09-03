// pthread_rwlock_linked_list.c
#include <stdio.h>
#include <stdlib.h>
#include <pthread.h>
#include <time.h>

typedef struct node {
    int data;
    struct node* next;
} node;

node* head = NULL;
pthread_rwlock_t rwlock;

int Member(int value) {
    pthread_rwlock_rdlock(&rwlock);
    node* curr = head;
    while (curr != NULL && curr->data < value)
        curr = curr->next;
    int result = (curr != NULL && curr->data == value);
    pthread_rwlock_unlock(&rwlock);
    return result;
}

int Insert(int value) {
    pthread_rwlock_wrlock(&rwlock);
    node** curr = &head;
    while (*curr != NULL && (*curr)->data < value)
        curr = &((*curr)->next);
    if (*curr == NULL || (*curr)->data != value) {
        node* new_node = malloc(sizeof(node));
        new_node->data = value;
        new_node->next = *curr;
        *curr = new_node;
        pthread_rwlock_unlock(&rwlock);
        return 1;
    }
    pthread_rwlock_unlock(&rwlock);
    return 0;
}

int Delete(int value) {
    pthread_rwlock_wrlock(&rwlock);
    node** curr = &head;
    while (*curr != NULL && (*curr)->data < value)
        curr = &((*curr)->next);
    if (*curr != NULL && (*curr)->data == value) {
        node* temp = *curr;
        *curr = temp->next;
        free(temp);
        pthread_rwlock_unlock(&rwlock);
        return 1;
    }
    pthread_rwlock_unlock(&rwlock);
    return 0;
}

typedef struct {
    int mMember, mInsert, mDelete, totalOps;
} thread_args;

void PopulateList(int n) {
    int inserted = 0;
    while (inserted < n) {
        int val = rand() % 65536;
        if (Insert(val))
            inserted++;
    }
}

void* ThreadWork(void* args) {
    thread_args* targs = (thread_args*)args;
    int i;
    for (i = 0; i < targs->totalOps; i++) {
        int opType;
        if (i < targs->mMember)
            opType = 0;
        else if (i < targs->mMember + targs->mInsert)
            opType = 1;
        else
            opType = 2;
        int val = rand() % 65536;
        if (opType == 0)
            Member(val);
        else if (opType == 1)
            Insert(val);
        else
            Delete(val);
    }
    return NULL;
}

int main(int argc, char* argv[]) {
    int n = 1000, m = 10000, thread_count = 4;
    double mMember = 0.99, mInsert = 0.005, mDelete = 0.005;
    if (argc > 1) thread_count = atoi(argv[1]);
    srand(time(NULL));
    pthread_rwlock_init(&rwlock, NULL);
    PopulateList(n);

    pthread_t* threads = malloc(thread_count * sizeof(pthread_t));
    thread_args* targs = malloc(thread_count * sizeof(thread_args));
    int memberOps = m * mMember, insertOps = m * mInsert, deleteOps = m * mDelete;
    int ops_per_thread = m / thread_count;

    for (int i = 0; i < thread_count; i++) {
        targs[i].mMember = memberOps / thread_count;
        targs[i].mInsert = insertOps / thread_count;
        targs[i].mDelete = deleteOps / thread_count;
        targs[i].totalOps = ops_per_thread;
    }

    clock_t start = clock();
    for (int i = 0; i < thread_count; i++)
        pthread_create(&threads[i], NULL, ThreadWork, &targs[i]);
    for (int i = 0; i < thread_count; i++)
        pthread_join(threads[i], NULL);
    clock_t end = clock();

    double elapsed = (double)(end - start) / CLOCKS_PER_SEC;
    printf("Elapsed time: %f seconds\n", elapsed);

    pthread_rwlock_destroy(&rwlock);
    free(threads);
    free(targs);
    return 0;
}