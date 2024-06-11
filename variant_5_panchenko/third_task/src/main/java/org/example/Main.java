package org.example;

import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

public class Main {
    private static final int CARS_NUM = 500;

    private static class Car implements Runnable {
        private static final int MIN_SLEEP_MILLIS = 100;
        private static final int MAX_SLEEP_MILLIS = 700;

        private final AtomicBoolean isCanMove;

        private Car(AtomicBoolean isCanMove) {
            this.isCanMove = isCanMove;
        }

        @Override
        public void run() {
            while(true) {



            }
        }
    }

    public static void main(String[] args) {
        final var isCanMove = new AtomicBoolean();
        final var pool = Executors.newFixedThreadPool(4);
        var futures = Future<?>[CARS_NUM];
        for(var i = 0; i < CARS_NUM; ++i) {
            var t = pool.submit(new Car(isCanMove));
        }
    }
}