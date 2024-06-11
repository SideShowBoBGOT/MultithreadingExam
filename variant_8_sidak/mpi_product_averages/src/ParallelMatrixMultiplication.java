import mpi.*;

public class ParallelMatrixMultiplication {
    public static void main(String[] args) {
        MPI.Init(args);
        int rank = MPI.COMM_WORLD.Rank();
        int size = MPI.COMM_WORLD.Size();

        int p = 7;
        double[][] A = new double[p][p];
        double[][] B = new double[p][p];
        double[] C = new double[p];
        double[] flattenedA = new double[p * p];
        double[] flattenedB = new double[p * p];

        if (rank == 0) {
            for (int i = 0; i < p; i++) {
                for (int j = 0; j < p; j++) {
                    A[i][j] = i + j;
                    B[i][j] = i - j;
                }
            }

            for (int i = 0; i < p; i++) {
                System.arraycopy(A[i], 0, flattenedA, i * p, p);
                System.arraycopy(B[i], 0, flattenedB, i * p, p);
            }
        }

        int[] counts = new int[size];
        int[] displacements = new int[size];
        int rowsPerProcess = p / size;
        int remainder = p % size;
        int offset = 0;

        for (int i = 0; i < size; i++) {
            counts[i] = (rowsPerProcess + (i < remainder ? 1 : 0)) * p;
            displacements[i] = offset;
            offset += counts[i];
        }

        int localRows = rowsPerProcess + (rank < remainder ? 1 : 0);
        double[] localA = new double[localRows * p];
        double[] localB = new double[localRows * p];

        MPI.COMM_WORLD.Scatterv(flattenedA, 0, counts, displacements, MPI.DOUBLE, localA, 0, localA.length, MPI.DOUBLE, 0);
        MPI.COMM_WORLD.Scatterv(flattenedB, 0, counts, displacements, MPI.DOUBLE, localB, 0, localB.length, MPI.DOUBLE, 0);

        double[] localC = new double[localRows];
        for (int i = 0; i < localRows; i++) {
            double sumA = 0;
            double sumB = 0;
            for (int j = 0; j < p; j++) {
                sumA += localA[i * p + j];
                sumB += localB[i * p + j];
            }
            localC[i] = (sumA / p) * (sumB / p);
        }

        int[] gatherCounts = new int[size];
        int[] gatherDisplacements = new int[size];
        offset = 0;

        for (int i = 0; i < size; i++) {
            gatherCounts[i] = rowsPerProcess + (i < remainder ? 1 : 0);
            gatherDisplacements[i] = offset;
            offset += gatherCounts[i];
        }

        MPI.COMM_WORLD.Gatherv(localC, 0, localRows, MPI.DOUBLE, C, 0, gatherCounts, gatherDisplacements, MPI.DOUBLE, 0);

        if (rank == 0) {
            System.out.println("Result matrix C:");
            for (int i = 0; i < p; i++) {
                System.out.println(C[i]);
            }
        }

        MPI.Finalize();
    }
}
