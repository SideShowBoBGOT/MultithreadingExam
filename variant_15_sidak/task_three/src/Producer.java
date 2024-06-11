import java.util.Random;

public class Producer implements Runnable {
    private Queue queue;

    public Producer(Queue queue) {
        this.queue = queue;
    }

    public void run() {
        var random = new Random();
        for (int i = 0; i < 1000; i++) {
            queue.put(random.nextInt(1000));
        }
    }
}

