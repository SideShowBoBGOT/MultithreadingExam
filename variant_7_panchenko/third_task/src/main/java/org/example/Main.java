package org.example;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Main {

    private static class Worker implements Runnable {
        private int index;

        Worker(final int index) {
            this.index = index;
        }

        @Override
        public void run() {
            System.out.println(index);
        }
    }

    private static int TOTAL_WORKERS = 100;

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        final var pool = Executors.newFixedThreadPool(10);
        var futures = new Future<?>[TOTAL_WORKERS];

        for(var i = 0; i < TOTAL_WORKERS; ++i) {
            futures[i] = pool.submit(new Worker(i));
        }

        for(var i = 0; i < TOTAL_WORKERS; ++i) {
            futures[i].get();
        }

        pool.shutdown();
    }
}