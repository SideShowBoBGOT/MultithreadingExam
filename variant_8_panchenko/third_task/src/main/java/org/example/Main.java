package org.example;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

public class Main {

    private static class LengthTask extends RecursiveTask<Integer> {
        private static final int THRESHOLD = 1000;
        private final List<String> words;
        private final int start;
        private final int end;

        LengthTask(final List<String> words, final int start, final int end) {
            this.words = words;
            this.start = start;
            this.end = end;
        }

        @Override
        protected Integer compute() {
            if (end - start <= THRESHOLD) {
                return computeDirectly();
            } else {
                int mid = (start + end) / 2;
                final var rhs = new LengthTask(words, mid, end);
                rhs.fork();
                final var lhs = new LengthTask(words, start, mid);

                final var lhsRes = lhs.compute();
                final var rhsRes = rhs.join();
                return lhsRes + rhsRes;
            }
        }

        private int computeDirectly() {
            var sum = 0;
            for (int i = start; i < end; i++) {
                sum += words.get(i).length();
            }
            return sum;
        }
    }

    public static void main(String[] args) throws IOException {
        String filePath = "wiki-100k.txt";
        final var words = Files.readAllLines(Paths.get(filePath));
        final var pool = ForkJoinPool.commonPool();
        final var totalLength = pool.invoke(new LengthTask(words, 0, words.size()));
        double averageLength = (double) totalLength / (double) words.size();
        System.out.println("Average word length: " + averageLength);
    }

}