import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Queue {
    private int putPtr, takePtr, count;
    private final int[] items;
    private final int itemsCount;
    private final ReentrantLock lock = new ReentrantLock();
    private final Condition notFull = lock.newCondition();
    private final Condition notEmpty = lock.newCondition();

    public Queue(int itemsCount) {
        this.itemsCount = itemsCount;
        putPtr = takePtr = count = 0;
        items = new int[itemsCount];
    }

    public int take() {
        lock.lock();
        try {
            while (count == 0) {
                notEmpty.await();
            }
            var value = items[takePtr];
            count--;
            System.out.println("Value " + value + " was taken from index " + takePtr + ". Total count: " + count);
            if (++takePtr == items.length) {
                takePtr = 0;
            }
            notFull.signal();
            return value;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
        }
    }

    public void put(int value) {
        lock.lock();
        try {
            while (count == itemsCount) {
                notFull.await();
            }
            items[putPtr] = value;
            count++;
            System.out.println("Value " + value + " was put at index " + putPtr + ". Total count: " + count);
            if (++putPtr == itemsCount) {
                putPtr = 0;
            }
            notEmpty.signal();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
        }
    }
}