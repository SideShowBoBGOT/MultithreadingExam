import mpi.*;

public class CascadeSum {

    private static final int ARRAY_SIZE = 8;

    public static void main(String[] args) {
        try {
            MPI.Init(args);

            int rank = MPI.COMM_WORLD.Rank();
            int size = MPI.COMM_WORLD.Size();

            int[] array = new int[ARRAY_SIZE];
            if (rank == 0) {
                for (int i = 0; i < ARRAY_SIZE; i++) {
                    array[i] = i + 1;
                }
            }

            int elementsPerProc = ARRAY_SIZE / size;
            int remainingElements = ARRAY_SIZE % size;

            int[] sendCounts = new int[size];
            int[] displs = new int[size];
            int offset = 0;

            for (int i = 0; i < size; i++) {
                sendCounts[i] = elementsPerProc + (i < remainingElements ? 1 : 0);
                displs[i] = offset;
                offset += sendCounts[i];
            }

            int recvCount = sendCounts[rank];
            int[] subArray = new int[recvCount];

            MPI.COMM_WORLD.Scatterv(array, 0, sendCounts, displs, MPI.INT, subArray, 0, recvCount, MPI.INT, 0);

            int localSum = 0;
            for (int i = 0; i < recvCount; i++) {
                localSum += subArray[i];
            }

            int step = 1;
            while (step < size) {
                if (rank % (2 * step) == 0) {
                    if (rank + step < size) {
                        int[] receivedSum = new int[1];
                        MPI.COMM_WORLD.Recv(receivedSum, 0, 1, MPI.INT, rank + step, 0);
                        localSum += receivedSum[0];
                    }
                } else {
                    int[] sendSum = {localSum};
                    MPI.COMM_WORLD.Send(sendSum, 0, 1, MPI.INT, rank - step, 0);
                    break;
                }
                step *= 2;
            }

            if (rank == 0) {
                System.out.println("Total sum: " + localSum);
            }

            MPI.Finalize();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
