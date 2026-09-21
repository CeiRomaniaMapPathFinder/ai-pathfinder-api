package com.ceiromania.pathfinder.services;

import com.ceiromania.pathfinder.dtos.Responsedtos;
import com.ceiromania.pathfinder.dtos.Routedtos;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BfsSearchTest {

    @Test
    void startEqualsEndReturnsTrivialRoute() {
        Routedtos route = Routedtos.builder().start("Arad").end("Arad").build();

        Responsedtos response = new BfsSearch(route).findRoute();

        assertEquals(List.of("Arad"), response.getPath());
        assertEquals(0, response.getDistance());
        assertEquals(1, response.getTotalNodes());
        assertEquals(Map.of(0, List.of("Arad")), response.getRoutes());
    }

    @Test
    void normalSearchStillWorks() {
        Routedtos route = Routedtos.builder().start("Arad").end("Bucharest").build();

        Responsedtos response = new BfsSearch(route).findRoute();

        assertEquals("Arad", response.getPath().get(0));
        assertEquals("Bucharest", response.getPath().get(response.getPath().size() - 1));
    }

    @Test
    void aradToBucharestHasFixedExpansionOrder() {
        Routedtos route = Routedtos.builder().start("Arad").end("Bucharest").build();

        Responsedtos response = new BfsSearch(route).findRoute();

        assertEquals(List.of("Arad", "Sibiu", "Timisoara", "Zerind", "Fagaras"), response.getExpanded());
        assertEquals(List.of("Arad", "Sibiu", "Fagaras", "Bucharest"), response.getPath());
        assertEquals(450, response.getDistance());
    }

    @Test
    void expandedFollowsEnqueueOrderOfRoutes() {
        Routedtos route = Routedtos.builder().start("Arad").end("Bucharest").build();

        Responsedtos response = new BfsSearch(route).findRoute();

        List<String> expanded = response.getExpanded();
        Map<Integer, List<String>> routes = response.getRoutes();
        List<String> enqueued = new ArrayList<>(List.of("Arad"));
        for (int k = 0; k < routes.size(); k++) {
            enqueued.addAll(routes.get(k));
        }

        for (int k = 0; k < expanded.size(); k++) {
            assertEquals(enqueued.get(k), expanded.get(k), "expanded[" + k + "]");
        }
        assertEquals(routes.size(), expanded.size());
        assertTrue(routes.get(routes.size() - 1).contains("Bucharest"));
        assertFalse(expanded.contains("Bucharest"));
    }
}
