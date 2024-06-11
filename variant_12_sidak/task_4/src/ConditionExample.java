import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ConditionExample {
    private final Lock lock = new ReentrantLock();
    private final Condition isEmpty = lock.newCondition();
    private final Condition isFull = lock.newCondition();
    private boolean empty = true;
    private boolean full = false;
    private int item;

    public static void main(String[] args) {
        ConditionExample example = new ConditionExample();
        Thread threadF = new Thread(new TaskF(example));
        threadF.start();
    }

    public void put(int value) throws InterruptedException {
        lock.lock();
        try {
            while (!empty) {
                isEmpty.await();
            }
            item = value;
            empty = false;
            full = true;
            System.out.println("Put item: " + item);
            isFull.signal();
        } finally {
            lock.unlock();
        }
    }

    public int take() throws InterruptedException {
        lock.lock();
        try {
            while (!full) {
                isFull.await();
            }
            int takenItem = item;
            empty = true;
            full = false;
            System.out.println("Took item: " + takenItem);
            isEmpty.signal();
            return takenItem;
        } finally {
            lock.unlock();
        }
    }

    static class TaskF implements Runnable {
        private final ConditionExample example;

        public TaskF(ConditionExample example) {
            this.example = example;
        }

        @Override
        public void run() {
            try {
                for (int i = 0; i < 10; i++) {
                    example.put(i);
                    Thread.sleep(1000);
                    example.take();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                e.printStackTrace();
            }
        }
    }
}

