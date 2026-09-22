package com.ceiromania.pathfinder.services;

import com.ceiromania.pathfinder.dtos.Nodedtos;
import com.ceiromania.pathfinder.dtos.Responseheuristicdtos;
import com.ceiromania.pathfinder.dtos.Routedtos;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class AStarSearchTest {

    /** start, goal, cost, cities expanded in order (= path for every row) */
    private static final String[][] HANDOFF_ROWS = {
            {"Arad", "Bucharest", "418", "Arad, Sibiu, Rimnicu Vilcea, Pitesti, Bucharest"},
            {"Zerind", "Bucharest", "493", "Zerind, Arad, Sibiu, Rimnicu Vilcea, Pitesti, Bucharest"},
            {"Zerind", "Sibiu", "215", "Zerind, Arad, Sibiu"},
            {"Timisoara", "Bucharest", "536", "Timisoara, Arad, Sibiu, Rimnicu Vilcea, Pitesti, Bucharest"},
            {"Neamt", "Bucharest", "406", "Neamt, Iasi, Vaslui, Urziceni, Bucharest"},
            {"Eforie", "Sibiu", "547", "Eforie, Hirsova, Urziceni, Bucharest, Pitesti, Rimnicu Vilcea, Sibiu"},
            {"Arad", "Neamt", "824", "Arad, Sibiu, Rimnicu Vilcea, Pitesti, Bucharest, Urziceni, Vaslui, Iasi, Neamt"},
            {"Craiova", "Iasi", "558", "Craiova, Pitesti, Bucharest, Urziceni, Vaslui, Iasi"},
            {"Lugoj", "Fagaras", "468", "Lugoj, Timisoara, Arad, Sibiu, Fagaras"},
            // the only f-tie on the map: f(Arad) = 75 + 409 = f(Oradea) = 71 + 413; Arad first gives the optimal 441
            {"Zerind", "Craiova", "441", "Zerind, Arad, Sibiu, Rimnicu Vilcea, Craiova"},
    };

    @Test
    void reproducesHandoffCostsExpansionsAndPaths() {
        for (String[] row : HANDOFF_ROWS) {
            String label = row[0] + " -> " + row[1];
            List<String> expected = List.of(row[3].split(", "));

            Responseheuristicdtos response = search(row[0], row[1]);

            assertEquals(Integer.parseInt(row[2]), response.getDistance(), label);
            assertEquals(expected, response.getPath(), label);
            assertEquals(expected, expansionOrder(response), label);
            assertEquals(expected.size(), response.getRoutes().size(), label);
        }
    }

    @Test
    void zerindToBucharestExpandsAradSecondAndNeverOradea() {
        List<String> expanded = expansionOrder(search("Zerind", "Bucharest"));

        assertEquals("Arad", expanded.get(1));
        assertFalse(expanded.contains("Oradea"));
    }

    @Test
    void totalNodesCountsDistinctCitiesThatEnteredTheFrontier() {
        assertEquals(10, search("Zerind", "Bucharest").getTotalNodes());
        assertEquals(10, search("Arad", "Bucharest").getTotalNodes());
        assertEquals(11, search("Eforie", "Sibiu").getTotalNodes());
        assertEquals(16, search("Arad", "Neamt").getTotalNodes());
    }

    @Test
    void startEqualsEndIsATrivialSearch() {
        Responseheuristicdtos response = search("Arad", "Arad");

        assertEquals(List.of("Arad"), response.getPath());
        assertEquals(0, response.getDistance());
        assertEquals(1, response.getTotalNodes());
        assertEquals(1, response.getRoutes().size());
        assertEquals(0, response.getRoutes().get(0).get(0).getExpandedAt());
    }

    @Test
    void traceCarriesGHFAndExpandedAt() {
        for (String[] row : HANDOFF_ROWS) {
            String label = row[0] + " -> " + row[1];
            Responseheuristicdtos response = search(row[0], row[1]);
            Map<Integer, List<Nodedtos>> route = response.getRoutes();

            for (int step = 0; step < route.size(); step++) {
                Nodedtos expanded = route.get(step).get(0);
                assertEquals(step, expanded.getExpandedAt(), label + " step " + step);
                assertEquals(expanded.getGN() + expanded.getHN(), expanded.getFN(), label + " step " + step);

                // every expanded city except the start was generated exactly once as a child marked with its step
                if (step > 0) {
                    List<Nodedtos> matches = new ArrayList<>();
                    for (List<Nodedtos> generated : route.values()) {
                        for (Nodedtos child : generated.subList(1, generated.size())) {
                            if (child.getExpandedAt() == step) {
                                matches.add(child);
                            }
                        }
                    }
                    assertEquals(1, matches.size(), label + " children marked with step " + step);
                    assertEquals(expanded.getTown(), matches.get(0).getTown(), label + " step " + step);
                    assertEquals(expanded.getGN(), matches.get(0).getGN(), label + " step " + step);
                }
            }

            Nodedtos goal = route.get(route.size() - 1).get(0);
            assertEquals(response.getDistance(), goal.getGN(), label + " g(goal) equals path cost");
            assertEquals(0, goal.getHN(), label + " h(goal)");
        }
    }

    private static Responseheuristicdtos search(String start, String end) {
        return new AStarSearch(Routedtos.builder().start(start).end(end).build()).findRoute();
    }

    private static List<String> expansionOrder(Responseheuristicdtos response) {
        List<String> order = new ArrayList<>();
        for (int step = 0; step < response.getRoutes().size(); step++) {
            order.add(response.getRoutes().get(step).get(0).getTown());
        }
        return order;
    }
}
