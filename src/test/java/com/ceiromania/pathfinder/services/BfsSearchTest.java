package com.ceiromania.pathfinder.services;

import com.ceiromania.pathfinder.dtos.Responsedtos;
import com.ceiromania.pathfinder.dtos.Routedtos;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
}
