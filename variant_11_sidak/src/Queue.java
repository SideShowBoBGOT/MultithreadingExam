import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

class Queue {
    private final ReentrantLock locker;
    private final Condition notEmpty;
    private final Condition notFull;
    private int putPtr, takePtr, count;
    private final int[] items;

    public Queue(int size) {
        locker = new ReentrantLock();
        notEmpty = locker.newCondition();
        notFull = locker.newCondition();
        putPtr = takePtr = count = 0;
        items = new int[size];
    }

    public int getCount() {
        return count;
    }

    public void take() {
        locker.lock();
        try {
            while (count == 0) {
                notEmpty.await();
            }
            count--;
            if (++takePtr == items.length) {
                takePtr = 0;
            }
        } catch (InterruptedException ex) {
            throw new RuntimeException(ex);
        } finally {
            locker.unlock();
        }
    }

    public void put(int item) {
        locker.lock();
        try {
            while (count == items.length) {
                notFull.await();
            }
            items[putPtr] = item;
            count++;
            if (++putPtr == items.length) {
                putPtr = 0;
            }
            notEmpty.signal();
        } catch (InterruptedException ex) {
            throw new RuntimeException(ex);
        } finally {
            locker.unlock();
        }
    }
}