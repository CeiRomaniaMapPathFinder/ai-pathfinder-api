package com.ceiromania.pathfinder.controllers;

import com.ceiromania.pathfinder.data.RomaniaMap;
import com.ceiromania.pathfinder.dtos.Comparisondtos;
import com.ceiromania.pathfinder.dtos.Measurementdtos;
import com.ceiromania.pathfinder.dtos.Routedtos;
import com.ceiromania.pathfinder.exceptions.RouteNotFoundException;
import com.ceiromania.pathfinder.services.AStarSearch;
import com.ceiromania.pathfinder.services.BfsSearch;
import com.ceiromania.pathfinder.services.SearchBenchmark;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ComparisonController {

    @GetMapping("/api/compare")
    public Comparisondtos compare(
            @RequestParam String start,
            @RequestParam String end
    ) {

        Routedtos route = Routedtos.builder()
                .start(start)
                .end(end)
                .build();

        if (!RomaniaMap.GRAPH.containsKey(route.getStart())) {
            throw new RouteNotFoundException(
                    "Start city not found: " + route.getStart()
            );
        }

        if (!RomaniaMap.GRAPH.containsKey(route.getEnd())) {
            throw new RouteNotFoundException(
                    "End city not found: " + route.getEnd()
            );
        }

        BfsSearch bfs = new BfsSearch(route);
        AStarSearch aStar = new AStarSearch(route);
        SearchBenchmark.Pair cost = SearchBenchmark.measure(bfs::searchWithoutTrace, aStar::searchWithoutTrace);

        return new Comparisondtos(toDto(cost.bfs()), toDto(cost.astar()));
    }

    private static Measurementdtos toDto(SearchBenchmark.Result result) {
        return new Measurementdtos(result.medianMillis(), result.medianKb(), result.runs());
    }
}
