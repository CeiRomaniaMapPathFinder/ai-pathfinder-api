package com.ceiromania.pathfinder.services;

import com.ceiromania.pathfinder.data.RomaniaMap;
import com.ceiromania.pathfinder.dtos.Nodedtos;
import com.ceiromania.pathfinder.dtos.Responseheuristicdtos;
import com.ceiromania.pathfinder.dtos.Routedtos;

import java.util.*;

/**
 * A* with the xGT-v2b heuristic: expand the frontier entry with the smallest f = g + h,
 * ties broken by insertion order, goal test when the goal is expanded (not when generated).
 * route[k] in the response = the city expanded at step k followed by every neighbour it generated.
 */
public class AStarSearch {

    private final Routedtos route;

    public AStarSearch(Routedtos route) {
        this.route = route;
    }

    /** One way of reaching a city. seq is the push counter so equal f expands in insertion order. */
    private record Entry(String city, int g, int h, int f, int seq) {
    }

    public Responseheuristicdtos findRoute() {
        String start = route.getStart();
        String goal = route.getEnd();
        int hStart = XgtHeuristic.h(goal, start); 

        long startTime = System.nanoTime();

        PriorityQueue<Entry> frontier = new PriorityQueue<>(
                Comparator.comparingInt(Entry::f).thenComparingInt(Entry::seq));
        Map<String, Integer> bestG = new HashMap<>();
        Map<String, String> parentMap = new HashMap<>();
        Map<String, Nodedtos> pushedAs = new HashMap<>(); // the trace entry each city was last pushed from
        Set<String> closed = new HashSet<>();
        Map<Integer, List<Nodedtos>> trace = new HashMap<>();

        int seq = 0;
        frontier.add(new Entry(start, 0, hStart, hStart, seq++));
        bestG.put(start, 0);

        int step = 0;
        while (!frontier.isEmpty()) {
            Entry current = frontier.poll();
            if (closed.contains(current.city())) {
                continue; // stale entry: this city was already expanded with a better g
            }
            closed.add(current.city());

            Nodedtos expanded = new Nodedtos(current.city(), current.g(), current.h(), current.f());
            expanded.setExpandedAt(step);
            if (pushedAs.containsKey(current.city())) {
                pushedAs.get(current.city()).setExpandedAt(step);
            }
            List<Nodedtos> generated = new ArrayList<>();
            generated.add(expanded);
            trace.put(step, generated);

            if (current.city().equals(goal)) {
                break;
            }

            for (Map.Entry<String, Integer> road : new TreeMap<>(RomaniaMap.GRAPH.get(current.city())).entrySet()) {
                String neighbor = road.getKey();
                int g = current.g() + road.getValue();
                int h = XgtHeuristic.h(goal, neighbor);
                Nodedtos child = new Nodedtos(neighbor, g, h, g + h);
                generated.add(child);

                if (!closed.contains(neighbor) && g < bestG.getOrDefault(neighbor, Integer.MAX_VALUE)) {
                    bestG.put(neighbor, g);
                    parentMap.put(neighbor, current.city());
                    pushedAs.put(neighbor, child);
                    frontier.add(new Entry(neighbor, g, h, g + h, seq++));
                }
            }
            step++;
        }

        long endTime = System.nanoTime();
        double durationInMs = (endTime - startTime) / 1_000_000.0;

        List<String> finalPath = reconstructPath(parentMap, goal);
        int totalDistance = calculateTotalDistance(finalPath);

        Responseheuristicdtos response = new Responseheuristicdtos(bestG.size(), totalDistance, durationInMs, finalPath);
        response.setRoute(trace);
        return response;
    }

    private List<String> reconstructPath(Map<String, String> parentMap, String end) {
        List<String> path = new ArrayList<>();
        String currentCity = end;

        while (currentCity != null) {
            path.add(currentCity);
            currentCity = parentMap.get(currentCity);
        }

        Collections.reverse(path);
        return path;
    }

    private int calculateTotalDistance(List<String> path) {
        int total = 0;
        for (int i = 0; i < path.size() - 1; i++) {
            total += RomaniaMap.GRAPH.get(path.get(i)).get(path.get(i + 1));
        }
        return total;
    }
}
