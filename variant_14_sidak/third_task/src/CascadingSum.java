import java.util.concurrent.RecursiveTask;
import java.util.concurrent.ForkJoinPool;

public class CascadingSum extends RecursiveTask<Long> {
    private static final int THRESHOLD = 1000;
    private final int[] array;
    private final int start;
    private final int end;

    public CascadingSum(int[] array, int start, int end) {
        this.array = array;
        this.start = start;
        this.end = end;
    }

    @Override
    protected Long compute() {
        int length = end - start;
        long sum;
        if (length <= THRESHOLD) {
            sum = computeDirectly();
        } else {
            int mid = start + length / 2;
            CascadingSum leftTask = new CascadingSum(array, start, mid);
            CascadingSum rightTask = new CascadingSum(array, mid, end);
            leftTask.fork();
            rightTask.fork();
            sum = rightTask.join() + leftTask.join();
        }
        return sum;
    }

    private long computeDirectly() {
        long sum = 0;
        for (int i = start; i < end; i++) {
            sum += array[i];
        }
        return sum;
    }

    public static void main(String[] args) {
        int[] array = new int[10000];
        for (int i = 0; i < array.length; i++) {
            array[i] = i + 1;
        }

        ForkJoinPool pool = new ForkJoinPool();
        CascadingSum task = new CascadingSum(array, 0, array.length);
        long result = pool.invoke(task);

        System.out.println("Sum: " + result);
    }
}
