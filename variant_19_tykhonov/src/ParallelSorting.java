import mpi.*;

public class ParallelSorting {
    public static void main(String[] args) {
        MPI.Init(args);
        int rank = MPI.COMM_WORLD.Rank();
        int size = MPI.COMM_WORLD.Size();

        String[] arrayA = {"apple", "banana", "orange", "grape", "kiwi", "mango", "pear", "peach", "plum"};
        int chunkSize = arrayA.length / size;
        String[] localChunk = new String[chunkSize];

        // Розсилка фрагментів масиву А між процесами
        MPI.COMM_WORLD.Scatter(arrayA, 0, chunkSize, MPI.OBJECT, localChunk, 0, chunkSize, MPI.OBJECT, 0 );

        // Сортування фрагментів масиву
        java.util.Arrays.sort(localChunk);

        String[] firstValues = new String[size];
        // Передача перших елементів відсортованих фрагментів масиву А головному процесу
        MPI.COMM_WORLD.Gather(new String[]{localChunk[0]}, 0, 1, MPI.OBJECT, firstValues, 0, 1, MPI.OBJECT, 0);

        if (rank == 0) {
            System.out.println("First values of sorted arrays: " + String.join(", ", firstValues));
        }

        MPI.Finalize();
    }
}

/*
MPI.COMM_WORLD - глобальний комунікатор, який використовується для спілкування між процесами.
MPI.COMM_WORLD.Rank() - повертає ранг поточного процесу.
MPI.COMM_WORLD.Size() - повертає кількість процесів у групі.
MPI.OBJECT - тип даних, який використовується для передачі об’єктів.
MPI.COMM_WORLD.Scatter() - розподіляє дані між всіма процесами у групі.
MPI.COMM_WORLD.Gather() - збирає дані з усіх процесів у групі.
MPI.Finalize() - використовується для завершення роботи MPI.
Процес з рангом 0 це головний процес, який розподіляє дані між іншими процесами, збирає результати та виводить їх.
Процеси воркери це процеси, які отримують дані від головного процесу, виконують обчислення
та відправляють результати головному процесу.
 */