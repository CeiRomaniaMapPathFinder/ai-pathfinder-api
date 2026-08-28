package com.ceiromania.pathfinder.controllers;

import com.ceiromania.pathfinder.data.RomaniaMap;
import com.ceiromania.pathfinder.dtos.Responsedtos;
import com.ceiromania.pathfinder.dtos.Routedtos;
import com.ceiromania.pathfinder.exceptions.RouteNotFoundException;
import com.ceiromania.pathfinder.services.BfsSearch;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class BlindSearchContoller {

    @GetMapping("/api/blind-search")
    public Responsedtos blindSearch(
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

        return bfs.findRoute();
    };

}