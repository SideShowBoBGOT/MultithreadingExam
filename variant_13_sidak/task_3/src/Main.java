public class Main {
    public static void main(String[] args) {
        var itemsCount = 1000;
        var drop = new Queue(itemsCount);
        var A = new Thread(new Producer(drop));
        var B = new Thread(new Producer(drop));
        var C = new Thread(new Consumer(drop));
        A.start();
        B.start();
        C.start();
    }
}