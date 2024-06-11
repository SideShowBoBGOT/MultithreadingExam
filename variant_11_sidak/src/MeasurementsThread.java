public class MeasurementsThread extends Thread {
    private final Queue queue;
    private final long measurementsTimeInterval;
    private int maxQueueLength;

    public MeasurementsThread(Queue queue, long measurementsTimeInterval) {
        this.queue = queue;
        this.measurementsTimeInterval = measurementsTimeInterval;
        maxQueueLength = 0;
    }

    public int getMaxQueueLength() {
        return maxQueueLength;
    }

    @Override
    public void run() {
        try {
            while (!Thread.interrupted()) {
                Thread.sleep(measurementsTimeInterval);
                maxQueueLength = Math.max(queue.getCount(), maxQueueLength);
            }
        } catch (InterruptedException e) {
        }
    }
}

