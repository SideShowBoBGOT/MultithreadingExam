public class Main {
    private static final int TOTAL_ITEMS = 40;
    private static volatile int itemsConsumed = 0;

    public static void main(String[] args) {
        BoundedQueue<Integer> queue = new BoundedQueue<>(10);

        Thread threadA = new Thread(() -> {
            try {
                for (int i = 0; i < 20; i++) {
                    queue.put(i);
                    System.out.println("Thread A added: " + i);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        Thread threadB = new Thread(() -> {
            try {
                for (int i = 20; i < 40; i++) {
                    queue.put(i);
                    System.out.println("Thread B added: " + i);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        Thread threadC = new Thread(() -> {
            try {
                while (true) {
                    Integer item = queue.take();
                    itemsConsumed++;
                    System.out.println("Thread C removed: " + item);
                    if (itemsConsumed == TOTAL_ITEMS) {
                        break;
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        threadA.start();
        threadB.start();
        threadC.start();

        try {
            threadA.join();
            threadB.join();
            threadC.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        System.out.println("All items have been processed.");
    }
}