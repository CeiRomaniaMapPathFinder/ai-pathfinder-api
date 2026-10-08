package com.ceiromania.pathfinder.services;

import java.util.Arrays;
import java.util.function.IntSupplier;

/**
 * Measures time and allocated memory of one search the same way for every algorithm.
 * The search is run without its animation trace, warmed up, then timed in batches;
 * each batch gives a per-search figure and the median batch is reported, so a single
 * GC pause or OS hiccup cannot decide the result. A single search takes about a
 * microsecond, too short to time on its own.
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

    /** search returns any value derived from its result, e.g. the path cost. */
    public static Result measure(IntSupplier search) {
        long checksum = 0;
        for (int i = 0; i < WARMUP_RUNS; i++) {
            checksum += search.getAsInt();
        }

        double[] millis = new double[BATCHES];
        double[] kb = new double[BATCHES];
        for (int batch = 0; batch < BATCHES; batch++) {
            long allocatedBefore = AllocationMeter.allocatedBytes();
            long startTime = System.nanoTime();
            for (int i = 0; i < RUNS_PER_BATCH; i++) {
                checksum += search.getAsInt();
            }
            long endTime = System.nanoTime();
            long allocatedAfter = AllocationMeter.allocatedBytes();

            millis[batch] = (endTime - startTime) / 1_000_000.0 / RUNS_PER_BATCH;
            double batchKb = AllocationMeter.kbBetween(allocatedBefore, allocatedAfter);
            kb[batch] = batchKb < 0 ? -1 : batchKb / RUNS_PER_BATCH;
        }
        sink = checksum;

        return new Result(median(millis), median(kb), BATCHES * RUNS_PER_BATCH);
    }

    private static double median(double[] values) {
        double[] sorted = values.clone();
        Arrays.sort(sorted);
        return sorted[sorted.length / 2];
    }
}
