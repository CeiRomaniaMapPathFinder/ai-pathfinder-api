package com.ceiromania.pathfinder.services;

import com.ceiromania.pathfinder.data.RomaniaMap;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Expected values are copied from HEURISTIC_IMPLEMENTATION_HANDOFF.md section 4. */
class XgtHeuristicTest {

    @Test
    void goalBucharestMatchesHandoffTable41() {
        assertEquals(expected("Bucharest 0, Urziceni 89, Giurgiu 90, Pitesti 106, Hirsova 193, "
                        + "Rimnicu Vilcea 212, Fagaras 219, Vaslui 237, Craiova 256, Eforie 279, Sibiu 302, "
                        + "Iasi 335, Drobeta 384, Neamt 422, Arad 453, Oradea 464, Mehadia 467, Zerind 531, "
                        + "Lugoj 542, Timisoara 584"),
                XgtHeuristic.table("Bucharest"));
    }

    @Test
    void goalSibiuMatchesHandoffTable42() {
        assertEquals(expected("Sibiu 0, Rimnicu Vilcea 84, Fagaras 99, Arad 151, Oradea 162, Pitesti 188, "
                        + "Zerind 229, Craiova 245, Timisoara 272, Bucharest 301, Drobeta 373, Urziceni 391, "
                        + "Giurgiu 391, Lugoj 393, Mehadia 454, Hirsova 495, Vaslui 539, Eforie 581, Iasi 636, "
                        + "Neamt 723"),
                XgtHeuristic.table("Sibiu"));
    }

    @Test
    void goalNeamtAndAradMatchHandoffSpotChecks43() {
        assertEquals(expected("Neamt 0, Iasi 92, Vaslui 185, Urziceni 337, Bucharest 430, Hirsova 441, "
                        + "Giurgiu 520, Eforie 527, Pitesti 536, Rimnicu Vilcea 642, Fagaras 649, Craiova 686, "
                        + "Sibiu 732, Drobeta 814, Arad 882, Oradea 894, Mehadia 897, Zerind 961, Lugoj 972, "
                        + "Timisoara 1014"),
                XgtHeuristic.table("Neamt"));
        assertEquals(expected("Arad 0, Zerind 82, Timisoara 121, Sibiu 156, Oradea 159, Rimnicu Vilcea 240, "
                        + "Lugoj 241, Fagaras 255, Mehadia 317, Pitesti 343, Drobeta 399, Craiova 407, "
                        + "Bucharest 457, Giurgiu 547, Urziceni 547, Hirsova 651, Vaslui 694, Eforie 737, "
                        + "Iasi 792, Neamt 879"),
                XgtHeuristic.table("Arad"));
    }

    @Test
    void everyGoalHasAFullTableWithZeroAtTheGoal() {
        for (String goal : RomaniaMap.GRAPH.keySet()) {
            Map<String, Integer> table = XgtHeuristic.table(goal);
            assertEquals(RomaniaMap.GRAPH.keySet(), table.keySet(), goal);
            assertEquals(0, XgtHeuristic.h(goal, goal), goal);
            table.forEach((city, h) -> assertTrue(h >= 0, goal + "/" + city));
        }
    }

    /** Parses the handoff's "City h, City h, ..." notation. */
    private static Map<String, Integer> expected(String spec) {
        Map<String, Integer> table = new HashMap<>();
        for (String item : spec.split(",")) {
            String entry = item.trim();
            int split = entry.lastIndexOf(' ');
            table.put(entry.substring(0, split), Integer.parseInt(entry.substring(split + 1)));
        }
        return table;
    }
}
