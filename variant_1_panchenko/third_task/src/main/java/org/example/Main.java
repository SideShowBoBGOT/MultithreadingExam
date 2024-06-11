package org.example;

import java.util.concurrent.atomic.AtomicBoolean;

public class Main {

    static private class STask implements Runnable {
        static private final int SLEEP_TIME_MILLIS = 1000;

        private final AtomicBoolean stopFlag;
        private final AtomicBoolean wakeUpFlag;

        private STask(final AtomicBoolean stopFlag, final AtomicBoolean wakeUpFlag) {
            this.stopFlag = stopFlag;
            this.wakeUpFlag = wakeUpFlag;
        }

        @Override
        public void run() {
            while(!stopFlag.get()) {
                try {
                    Thread.sleep(SLEEP_TIME_MILLIS);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                final var value = !wakeUpFlag.get();
                wakeUpFlag.set(value);
                if(value) {
                    synchronized(wakeUpFlag) {
                        wakeUpFlag.notify();
                    }
                }
            }
        }
    }

    static private class WTask implements Runnable {
        static private final int ITERATIONS_NUM = 30;
        static private final int SLEEP_TIME_MILLIS = 100;

        private final AtomicBoolean stopFlag;
        private final AtomicBoolean wakeUpFlag;

        private WTask(final AtomicBoolean stopFlag, final AtomicBoolean wakeUpFlag) {
            this.stopFlag = stopFlag;
            this.wakeUpFlag = wakeUpFlag;
        }

        @Override
        public void run() {
            for(var i = ITERATIONS_NUM; i > 0; --i) {
                System.out.println(i);
                try {
                    Thread.sleep(SLEEP_TIME_MILLIS);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                if(!wakeUpFlag.get()) {
                    try {
                        synchronized(wakeUpFlag) {
                            wakeUpFlag.wait();
                        }
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            stopFlag.set(true);
        }
    }

    public static void main(String[] args) throws InterruptedException {

        final var stopFlag = new AtomicBoolean(false);
        final var wakeUpFlag = new AtomicBoolean(true);

        final var s = new Thread(new STask(stopFlag, wakeUpFlag));
        final var w = new Thread(new WTask(stopFlag, wakeUpFlag));

        s.start();
        w.start();

        s.join();
        w.join();

        System.out.println("Finished");
    }

}