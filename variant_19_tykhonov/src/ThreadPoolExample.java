import java.util.concurrent.*;

public class ThreadPoolExample {
    private static int counter = 0;

    public static void main(String[] args) {

        // Створення пулу потоків із 10 потоками
        ExecutorService pool = Executors.newFixedThreadPool(10);
        // Завантаження воркерів збільшення
        for (int i = 0; i < 100; i++) {
            pool.submit(() -> {
                synchronized (ThreadPoolExample.class) {
                    counter++;
                }
            });
        }
        // Завантаження воркерів зменшення
        for (int i = 0; i < 10; i++) {
            pool.submit(() -> {
                synchronized (ThreadPoolExample.class) {
                    counter -= 10;
                }
            });
        }

        pool.shutdown();
        try {
            pool.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Final counter value: " + counter);
    }
}


/*
ExecutorService використовується для створення пулу потоків,
які можуть виконувати завдання одночасно. Він керує життєвим
циклом потоків і забезпечує ефективне використання системних ресурсів.
Executors - клас, який надає фабричні методи для створення різних типів пулів потоків.

newFixedThreadPool(int n) - створює пул потоків з фіксованою кількістю потоків n.

executor.submit() - використовується для відправки завдання на виконання в пул потоків.
Він повертає Future об’єкт, який можна використовувати для отримання результату завдання.

executor.shutdown() - використовується для завершення роботи пулу потоків після завершення виконання всіх завдань.
Це запобігає надсиланню будь-яких нових завдань до пулу.

executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS) - чекає завершення всіх завдань.

synchronized (ThreadPoolExample.class) - використовується для синхронізації доступу до спільних ресурсів (змінної counter).
race conditions - виникають, коли два або більше потоків намагаються змінити спільний ресурс одночасно.
*/