class CashierTask implements Runnable {

    private final Queue queue;
    private boolean stopFlag;

    public CashierTask(Queue queue) {
        this.queue = queue;
        this.stopFlag = false;
    }

    public void stop() {
        stopFlag = true;
    }

    @Override
    public void run() {
        try {
            while (!Thread.interrupted() && !stopFlag) {
                queue.take();
                var processingTime = 10;
                Thread.sleep(processingTime);
            }
        } catch (InterruptedException e) {
            System.out.println("Consumer task was interrupted");
        }
    }
}