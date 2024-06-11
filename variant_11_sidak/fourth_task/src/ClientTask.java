class ClientTask implements Runnable {
    private final Queue queue;
    private final int integerItem;
    private boolean stopFlag;

    public ClientTask(Queue queue) {
        this.queue = queue;
        integerItem = 69;
        this.stopFlag = false;
    }

    public void stop() {
        stopFlag = true;
    }

    @Override
    public void run() {
        try {
            while (!Thread.interrupted() && !stopFlag) {
                var producingTime = 1;
                Thread.sleep(producingTime);
                queue.put(integerItem);
            }
        } catch (InterruptedException e) {
            System.out.println("Producer task was interrupted");
        }
    }
}