package org.example;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

public class Main {

    private static class LengthTask extends RecursiveTask<Integer> {
        private static final int THRESHOLD = 400;
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
        String[] fileNames = {"1.txt", "2.txt"};
        List<String> all_words = new ArrayList<>();
        for(var fileName : fileNames) {
            final var words = Files.readAllLines(Paths.get(fileName));
            all_words.addAll(words);
        }
        final var pool = ForkJoinPool.commonPool();
        final var totalLength = pool.invoke(new LengthTask(all_words, 0, all_words.size()));
        double averageLength = (double) totalLength / (double) all_words.size();
        System.out.println("Average word length: " + averageLength);
    }

}