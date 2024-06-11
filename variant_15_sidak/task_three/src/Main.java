public class Main {
    public static void main(String[] args) {
        var itemsCount = 1000;
        var drop = new Queue(itemsCount);
        var P = new Thread(new Producer(drop));
        var R = new Thread(new Producer(drop));
        var Q = new Thread(new Consumer(drop));
        var S = new Thread(new Consumer(drop));
        P.start();
        R.start();
        Q.start();
        S.start();
    }
}