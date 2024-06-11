package org.example;

import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

public class Main {
    private static final Random RANDOM = new Random();
    private static final int TOTAL_RECORDS = 1000000;
    private static final int TOTAL_USERS = 10000;
    private static final int MIN_DAY = 1;
    private static final int MAX_DAY = 365;
    private static final int MIN_PROD = 1;
    private static final int MAX_PROD = 40;
    private static final int LOWER_BOUND = 12;

    private record Buyer(int id, int day, int total_products) {}

    private static Buyer generateBuyer() {
        return new Buyer(RANDOM.nextInt(0, TOTAL_USERS), RANDOM.nextInt(MIN_DAY, MAX_DAY), RANDOM.nextInt(MIN_PROD, MAX_PROD));
    }

    private static Buyer[] generateData() {
        var buyers = new Buyer[TOTAL_RECORDS];
        for(var i = 0; i < buyers.length; ++i) {
            buyers[i] = generateBuyer();
        }
        return buyers;
    }

    private record DataPair(int maxProd, int numLowerBoundProd) {}

    private static class SomeTask extends RecursiveTask<DataPair> {
        private final Buyer[] buyersData;
        private final int startIndex;
        private final int endIndex;

        private SomeTask(final Buyer[] buyersData, final int startIndex, final int endIndex) {
            this.buyersData = buyersData;
            this.startIndex = startIndex;
            this.endIndex = endIndex;
        }

        @Override
        protected DataPair compute() {
            final var dist = endIndex - startIndex;
            switch(dist) {
                case 1: {
                    final var first = buyersData[startIndex];
                    return new DataPair(first.total_products, first.total_products > LOWER_BOUND ? 0 : 1);
                }
                case 2: {
                    final var first = buyersData[startIndex];
                    final var second = buyersData[endIndex - 1];

                    final var maxProd = Math.max(first.total_products, second.total_products);
                    int numLowerBoundProd = 0;
                    if(first.total_products > LOWER_BOUND) {
                        numLowerBoundProd++;
                    }
                    if(second.total_products > LOWER_BOUND) {
                        numLowerBoundProd++;
                    }
                    return new DataPair(maxProd, numLowerBoundProd);
                }
                default: {

                    final var chunkSize = dist / 2;
                    final var splitIndex = startIndex + chunkSize;

                    final var rhs = new SomeTask(buyersData, splitIndex, endIndex);
                    rhs.fork();

                    final var lhs = new SomeTask(buyersData, startIndex, splitIndex);
                    final var lhsResult = lhs.compute();

                    try {
                        final var rhsResult = rhs.get();
                        return new DataPair(
                                Math.max(lhsResult.maxProd, rhsResult.maxProd),
                                lhsResult.numLowerBoundProd + rhsResult.numLowerBoundProd
                        );
                    } catch (InterruptedException | ExecutionException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }

    public static void main(String[] args) {

        final var data = generateData();

        final var pool = ForkJoinPool.commonPool();
        var res = pool.invoke(new SomeTask(data, 0, data.length));
        System.out.println(res);
    }

}