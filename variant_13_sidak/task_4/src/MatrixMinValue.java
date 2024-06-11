import mpi.*;

public class MatrixMinValue {
    public static void main(String[] args) throws MPIException {
        MPI.Init(args);

        int size = MPI.COMM_WORLD.Size();

        int rank = MPI.COMM_WORLD.Rank();

        int[][] A = {
                {9, 8, 7, 6},
                {5, -1488, 3, 2},
                {1, 0, -1, -2},
                {-3, -4, -5, -6}
        };

        int rows = A.length;
        int cols = A[0].length;
        int totalElements = rows * cols;
        int elementsPerProcess = totalElements / size;
        int remainingElements = totalElements % size;

        int[] flatMatrix = new int[totalElements];
        int index = 0;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                flatMatrix[index++] = A[i][j];
            }
        }

        int[] sendCounts = new int[size];
        int[] displs = new int[size];
        for (int i = 0; i < size; i++) {
            sendCounts[i] = elementsPerProcess + (i < remainingElements ? 1 : 0);
            displs[i] = (i == 0) ? 0 : displs[i - 1] + sendCounts[i - 1];
        }

        int[] buffer = new int[sendCounts[rank]];

        MPI.COMM_WORLD.Scatterv(flatMatrix, 0, sendCounts, displs, MPI.INT, buffer, 0, sendCounts[rank], MPI.INT, 0);

        int localMin = Integer.MAX_VALUE;
        for (int i = 0; i < buffer.length; i++) {
            if (buffer[i] < localMin) {
                localMin = buffer[i];
            }
        }

        int[] allMinValues = new int[size];
        MPI.COMM_WORLD.Gather(new int[]{localMin}, 0, 1, MPI.INT, allMinValues, 0, 1, MPI.INT, 0);

        if (rank == 0) {
            int globalMin = Integer.MAX_VALUE;
            for (int minValue : allMinValues) {
                if (minValue < globalMin) {
                    globalMin = minValue;
                }
            }
            System.out.println("The minimum value in the matrix is: " + globalMin);
        }

        MPI.Finalize();
    }
}

