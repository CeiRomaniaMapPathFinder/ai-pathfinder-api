package com.ceiromania.pathfinder.services;

import java.util.Arrays;
import java.util.function.IntSupplier;

/**
 * Measures time and allocated memory of BFS and A* for one route in the same call, so both run
 * on the same machine, under the same load and the same JIT state. The searches run without their
 * animation trace, are warmed up together, then timed in batches taking turns, and who goes first
 * swaps every round. A single search takes about a microsecond, too short to time on its own.
 */
public final class SearchBenchmark {

    static final int WARMUP_RUNS = 2_000;
    static final int BATCHES = 15;
    static final int RUNS_PER_BATCH = 1_000;

    // Keeps the JIT from dropping a search whose result would otherwise be unused.
    private static volatile long sink;

    private SearchBenchmark() {
    }

    /** medianMillis / medianKb are per search; runs is how many timed searches they come from. */
    public record Result(double medianMillis, double medianKb, int runs) {
    }

    public record Pair(Result bfs, Result astar) {
    }

    /** Each search returns any value derived from its result, e.g. the path cost. */
    public static Pair measure(IntSupplier bfs, IntSupplier astar) {
        long checksum = 0;
        for (int i = 0; i < WARMUP_RUNS; i++) {
            checksum += bfs.getAsInt();
            checksum += astar.getAsInt();
        }

        IntSupplier[] searches = {bfs, astar};
        double[][] millis = new double[2][BATCHES];
        double[][] kb = new double[2][BATCHES];
        for (int batch = 0; batch < BATCHES; batch++) {
            for (int turn = 0; turn < 2; turn++) {
                int which = (batch + turn) % 2;
                long allocatedBefore = AllocationMeter.allocatedBytes();
                long startTime = System.nanoTime();
                for (int i = 0; i < RUNS_PER_BATCH; i++) {
                    checksum += searches[which].getAsInt();
                }
                long endTime = System.nanoTime();
                long allocatedAfter = AllocationMeter.allocatedBytes();

                millis[which][batch] = (endTime - startTime) / 1_000_000.0 / RUNS_PER_BATCH;
                double batchKb = AllocationMeter.kbBetween(allocatedBefore, allocatedAfter);
                kb[which][batch] = batchKb < 0 ? -1 : batchKb / RUNS_PER_BATCH;
            }
        }
        sink = checksum;

        return new Pair(result(millis[0], kb[0]), result(millis[1], kb[1]));
    }

    private static Result result(double[] millis, double[] kb) {
        return new Result(median(millis), median(kb), BATCHES * RUNS_PER_BATCH);
    }

    private static double median(double[] values) {
        double[] sorted = values.clone();
        Arrays.sort(sorted);
        return sorted[sorted.length / 2];
    }
}
