#include <stdio.h>
#include <stdlib.h>
#include <mpi.h>
#include <time.h>

#define ARRAY_SIZE 100

void shuffle(int* array, int size) {
    srand(time(NULL));
    for (int i = size - 1; i > 0; i--) {
        int j = rand() % (i + 1);
        int temp = array[i];
        array[i] = array[j];
        array[j] = temp;
    }
}

void sort(int* array, int size) {
    for (int i = 0; i < size - 1; i++) {
        for (int j = 0; j < size - i - 1; j++) {
            if (array[j] > array[j + 1]) {
                int temp = array[j];
                array[j] = array[j + 1];
                array[j + 1] = temp;
            }
        }
    }
}

int main(int argc, char** argv) {
    int rank, size;
    int* global_array = NULL;
    int* local_array = NULL;
    int local_size;

    MPI_Init(&argc, &argv);
    MPI_Comm_rank(MPI_COMM_WORLD, &rank);
    MPI_Comm_size(MPI_COMM_WORLD, &size);
    local_size = ARRAY_SIZE / size;

    if (rank == 0) {
        global_array = (int*)malloc(ARRAY_SIZE * sizeof(int));
        for (int i = 0; i < ARRAY_SIZE; i++) {
            global_array[i] = i;
        }
        shuffle(global_array, ARRAY_SIZE);
    }
    local_array = (int*)malloc(local_size * sizeof(int));
    MPI_Scatter(global_array, local_size, MPI_INT, local_array, local_size, MPI_INT, 0, MPI_COMM_WORLD);
    sort(local_array, local_size);
    printf("Process %d: %d \n", rank, local_array[0]);
    free(local_array);
    if (rank == 0) {
        free(global_array);
    }
    MPI_Finalize();
    return 0;
}