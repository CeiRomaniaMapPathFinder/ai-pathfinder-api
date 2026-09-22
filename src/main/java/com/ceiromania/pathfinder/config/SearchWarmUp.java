package com.ceiromania.pathfinder.config;

import com.ceiromania.pathfinder.dtos.Routedtos;
import com.ceiromania.pathfinder.services.AStarSearch;
import com.ceiromania.pathfinder.services.BfsSearch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class SearchWarmUp implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(SearchWarmUp.class);

    @Override
    public void run(ApplicationArguments args) {
        Routedtos route = Routedtos.builder().start("Arad").end("Bucharest").build();
        warm("BFS", () -> new BfsSearch(route).findRoute());
        warm("A*", () -> new AStarSearch(route).findRoute());
    }

    private void warm(String name, Runnable search) {
        try {
            search.run();
            log.info("Warmed up {} search", name);
        } catch (Exception | LinkageError e) {
            log.warn("Warm-up of {} search failed, continuing: {}", name, e.toString());
        }
    }
}
