public class Main {

    public static void main(String[] args) {
        var massServiceSystem = new MassServiceSystem();
        Integer maxQueueLength = massServiceSystem.getMaxQueueLength();
        System.out.println("Max queue length: " + maxQueueLength);
    }
}