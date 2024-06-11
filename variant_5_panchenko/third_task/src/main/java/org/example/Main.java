package org.example;

import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

public class Main {

    private static class CarTraffic implements Runnable {
        private static final int MIN_SLEEP_MILLIS = 100;
        private static final int MAX_SLEEP_MILLIS = 200;
        private static final int CARS_NUM = 500;
        private static Random RANDOM = new Random();

        private final AtomicBoolean isCanMove;
        private final AtomicBoolean isAllCarsPassed;

        private CarTraffic(AtomicBoolean isCanMove, AtomicBoolean isAllCarsPassed) {
            this.isCanMove = isCanMove;
            this.isAllCarsPassed = isAllCarsPassed;
        }

        @Override
        public void run() {
            for(var i = 0; i < CARS_NUM; ++i) {
                if(!isCanMove.get()) {
                    synchronized(isCanMove) {
                        try {
                            isCanMove.wait();
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
                try {
                    Thread.sleep(RANDOM.nextInt(MIN_SLEEP_MILLIS, MAX_SLEEP_MILLIS));
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                System.out.printf("Car %d passed\n", i);
            }
            isAllCarsPassed.set(true);
        }
    }

    private static final int LIGHT_SWAP_TIME_MILLIS = 1000;

    public static void main(String[] args) throws InterruptedException {
        final var isCanMove = new AtomicBoolean(false);
        final var isAllCarsPassed = new AtomicBoolean(false);
        final var trafficThread = new Thread(new CarTraffic(isCanMove, isAllCarsPassed));
        trafficThread.start();

        while(!isAllCarsPassed.get()) {
            final var value = !isCanMove.get();
            isCanMove.set(value);
            if(value) {
                synchronized(isCanMove) {
                    isCanMove.notify();
                }
            }
            System.out.printf("Traffic light: %b", value);
            Thread.sleep(LIGHT_SWAP_TIME_MILLIS);
        }
    }
}