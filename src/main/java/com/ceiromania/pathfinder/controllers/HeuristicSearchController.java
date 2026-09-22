package com.ceiromania.pathfinder.controllers;

import com.ceiromania.pathfinder.data.RomaniaMap;
import com.ceiromania.pathfinder.dtos.Responseheuristicdtos;
import com.ceiromania.pathfinder.dtos.Routedtos;
import com.ceiromania.pathfinder.exceptions.RouteNotFoundException;
import com.ceiromania.pathfinder.services.AStarSearch;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HeuristicSearchController {

    @GetMapping("/api/heuristic-search")
    public Responseheuristicdtos heuristicSearch(
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

        AStarSearch aStar = new AStarSearch(route);

        return aStar.findRoute();
    }

}
