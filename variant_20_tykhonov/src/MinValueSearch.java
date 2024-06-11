import mpi.*;

import java.util.Arrays;

public class MinValueSearch {
    public static void main(String[] args) throws MPIException {
        MPI.Init(args);
        int rank = MPI.COMM_WORLD.Rank();
        int size = MPI.COMM_WORLD.Size();

        int[] C = {9, 5, 3, 8, 2, 7, 1, 6, 4};
        int fragmentSize = C.length / (size - 1);
        int[] fragment = new int[fragmentSize];

        if (rank == 0) { // Майстер

            for (int i = 1; i < size; i++) {
                int startIndex = (i - 1) * fragmentSize;
                MPI.COMM_WORLD.Send(C, startIndex, fragmentSize, MPI.INT, i, 0);
            }

            int[] minValues = new int[size - 1];
            for (int i = 1; i < size; i++) {
                MPI.COMM_WORLD.Recv(minValues, i - 1, 1, MPI.INT, i, 0);
            }

            System.out.println("Minimum values: " + Arrays.toString(minValues));

        } else { // Воркер

            MPI.COMM_WORLD.Recv(fragment, 0, fragmentSize, MPI.INT, 0, 0);

            int minValue = fragment[0];
            for (int i = 1; i < fragmentSize; i++) {
                if (fragment[i] < minValue) {
                    minValue = fragment[i];
                }
            }

            MPI.COMM_WORLD.Send(new int[]{minValue}, 0, 1, MPI.INT, 0, 0);
        }

        MPI.Finalize();
    }
}

/*
* MPI.COMM_WORLD.Send() - використовується для відправки даних іншому процесу.
* Параметри цього методу: дані, початковий індекс, кількість елементів, тип даних, ранг процесу, тег.
* MPI.COMM_WORLD.Recv() - використовується для отримання даних від іншого процесу.
* Параметри цього методу: масив для зберігання даних, початковий індекс, кількість елементів, тип даних, ранг процесу, тег.
*
* Процес з рангом 0 це головний процес, який розподіляє дані між іншими процесами, збирає результати та виводить їх.
* Процеси воркери це процеси, які отримують дані від головного процесу, виконують обчислення
* та відправляють результати головному процесу.
*
* MPI.init() - ініціалізує MPI.
* MPI.Finalize() - завершує роботу MPI.
* MPI.COMM_WORLD.Rank() - повертає ранг поточного процесу.
* MPI.COMM_WORLD.Size() - повертає кількість процесів у групі.
* MPI.INT - тип даних, який використовується для передачі цілих чисел.
* */