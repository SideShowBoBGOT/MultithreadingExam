import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ParallelSum {
    private static final int ARRAY_SIZE = 10000;
    private static final int NUM_TASKS = 100;
    private static final int NUM_THREADS = 4;

    public static void main(String[] args) throws Exception {
        double[] array = new double[ARRAY_SIZE];
        for (int i = 0; i < ARRAY_SIZE; i++) {
            array[i] = 1;
        }

        ExecutorService executorService = Executors.newFixedThreadPool(NUM_THREADS);
        Future<Double>[] futures = new Future[NUM_TASKS];

        int taskSize = ARRAY_SIZE / NUM_TASKS;
        for (int i = 0; i < NUM_TASKS; i++) {
            int startIndex = i * taskSize;
            int endIndex = (i == NUM_TASKS - 1) ? ARRAY_SIZE : (i + 1) * taskSize;
            futures[i] = executorService.submit(new SumTask(array, startIndex, endIndex));
        }

        double totalSum = 0;
        for (Future<Double> future : futures) {
            totalSum += future.get();
        }

        executorService.shutdown();

        System.out.println("Total sum: " + totalSum);
    }

    private static class SumTask implements Callable<Double> {
        private final double[] array;
        private final int startIndex;
        private final int endIndex;

        public SumTask(double[] array, int startIndex, int endIndex) {
            this.array = array;
            this.startIndex = startIndex;
            this.endIndex = endIndex;
        }

        @Override
        public Double call() {
            double sum = 0;
            for (int i = startIndex; i < endIndex; i++) {
                sum += array[i];
            }
            return sum;
        }
    }
}