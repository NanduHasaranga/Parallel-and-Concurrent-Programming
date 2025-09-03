// serial_linked_list.c
#include <stdio.h>
#include <stdlib.h>
#include <time.h>

typedef struct node {
    int data;
    struct node* next;
} node;

node* head = NULL;

int Member(int value) {
    node* curr = head;
    while (curr != NULL && curr->data < value)
        curr = curr->next;
    return (curr != NULL && curr->data == value);
}

int Insert(int value) {
    node** curr = &head;
    while (*curr != NULL && (*curr)->data < value)
        curr = &((*curr)->next);
    if (*curr == NULL || (*curr)->data != value) {
        node* new_node = malloc(sizeof(node));
        new_node->data = value;
        new_node->next = *curr;
        *curr = new_node;
        return 1;
    }
    return 0;
}

int Delete(int value) {
    node** curr = &head;
    while (*curr != NULL && (*curr)->data < value)
        curr = &((*curr)->next);
    if (*curr != NULL && (*curr)->data == value) {
        node* temp = *curr;
        *curr = temp->next;
        free(temp);
        return 1;
    }
    return 0;
}

void PopulateList(int n) {
    int inserted = 0;
    while (inserted < n) {
        int val = rand() % 65536;
        if (Insert(val))
            inserted++;
    }
}

void RunOperations(int m, double mMember, double mInsert, double mDelete) {
    int memberOps = m * mMember;
    int insertOps = m * mInsert;
    int deleteOps = m * mDelete;
    int totalOps = memberOps + insertOps + deleteOps;
    int i;
    for (i = 0; i < totalOps; i++) {
        int opType;
        if (i < memberOps)
            opType = 0;
        else if (i < memberOps + insertOps)
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
}

int main(int argc, char* argv[]) {
    int n = 1000, m = 10000;
    double mMember = 0.99, mInsert = 0.005, mDelete = 0.005;
    srand(time(NULL));
    PopulateList(n);

    clock_t start = clock();
    RunOperations(m, mMember, mInsert, mDelete);
    clock_t end = clock();

    double elapsed = (double)(end - start) / CLOCKS_PER_SEC;
    printf("Elapsed time: %f seconds\n", elapsed);
    return 0;
}