import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
class MassServiceSystem {

    public MassServiceSystem() {
    }

    public Integer getMaxQueueLength() {
        try {
            int numberOfChannels = 9;
            int queueSize = 100;
            long measurementsTimeInterval = 10;

            ExecutorService executor = Executors.newFixedThreadPool(numberOfChannels + 1);
            var queue = new Queue(queueSize);

            var clientTask = new ClientTask(queue);
            executor.execute(clientTask);

            var cashierTasks = new CashierTask[numberOfChannels];
            for (int i = 0; i < numberOfChannels; i++) {
                cashierTasks[i] = new CashierTask(queue);
                executor.execute(cashierTasks[i]);
            }

            var statisticThread = new MeasurementsThread(queue, measurementsTimeInterval);
            statisticThread.start();

            Thread.sleep(480);

            executor.shutdown();
            clientTask.stop();
            for (int i = 0; i < numberOfChannels; i++) {
                cashierTasks[i].stop();
            }
            statisticThread.interrupt();

            return statisticThread.getMaxQueueLength();

        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
        return null;
    }
}
