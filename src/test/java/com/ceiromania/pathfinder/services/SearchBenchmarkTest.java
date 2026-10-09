package com.ceiromania.pathfinder.services;

import com.ceiromania.pathfinder.dtos.Routedtos;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SearchBenchmarkTest {

    @Test
    void measuresBothSearchesWithTheSameNumberOfRuns() {
        Routedtos route = Routedtos.builder().start("Arad").end("Bucharest").build();
        SearchBenchmark.Pair pair = SearchBenchmark.measure(
                new BfsSearch(route)::searchWithoutTrace, new AStarSearch(route)::searchWithoutTrace);

        for (SearchBenchmark.Result result : new SearchBenchmark.Result[]{pair.bfs(), pair.astar()}) {
            assertEquals(SearchBenchmark.BATCHES * SearchBenchmark.RUNS_PER_BATCH, result.runs());
            assertTrue(result.medianMillis() > 0);
            assertTrue(result.medianKb() > 0);
        }
    }
}
