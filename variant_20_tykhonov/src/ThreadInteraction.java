import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ThreadInteraction {
    private static volatile boolean stateA = true;
    private static Lock lock = new ReentrantLock();

    public static void main(String[] args) {
        int[] array = {1, 2, 3};

        Thread threadA = new Thread(() -> {
            while (true) {
                try {Thread.sleep(100);}
                catch (InterruptedException e) {e.printStackTrace();}
                lock.lock();
                try {
                    stateA = !stateA;
                    System.out.println("Thread A: State changed to " + stateA);
                } finally {
                    lock.unlock();
                }
            }
        });

        Thread threadB = new Thread(() -> {
            while (true) {
                lock.lock();
                try {
                    if (stateA) {
                        System.out.println("Thread B: State of A is true");
                        for (int value : array) {
                            System.out.println("Thread B: " + value);
                        }
                        try {Thread.sleep(10);}
                        catch (InterruptedException e) {e.printStackTrace();}
                    }
                } finally {
                    lock.unlock();
                }
            }
        });

        threadA.start();
        threadB.start();
    }
}