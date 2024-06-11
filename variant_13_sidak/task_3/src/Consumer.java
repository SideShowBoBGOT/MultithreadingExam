public class Consumer implements Runnable {
    private Queue drop;

    public Consumer(Queue queue) {
        this.drop = queue;
    }

    public void run() {
        for (int i = 0; i < 1000; i++) {
            drop.take();
        }
    }
}
