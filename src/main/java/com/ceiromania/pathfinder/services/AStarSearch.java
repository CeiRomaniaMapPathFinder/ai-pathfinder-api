package com.ceiromania.pathfinder.services;

import com.ceiromania.pathfinder.data.RomaniaMap;
import com.ceiromania.pathfinder.dtos.Nodedtos;
import com.ceiromania.pathfinder.dtos.Responseheuristicdtos;
import com.ceiromania.pathfinder.dtos.Routedtos;

import java.util.*;

public class AStarSearch {

    private final Routedtos route;

    public AStarSearch(Routedtos route) {
        this.route = route;
    }

    private record Entry(String city, int g, int h, int f, int seq) {
    }

    private record Outcome(List<String> path, int distance, int totalNodes, int nodesExpanded,
                           int nodesGenerated, int peakNodesStored, Map<Integer, List<Nodedtos>> routes) {
    }

    public Responseheuristicdtos findRoute() {
        Outcome outcome = search(true);

        Responseheuristicdtos response = new Responseheuristicdtos(
                outcome.totalNodes(), outcome.distance(), outcome.path());
        response.setRoutes(outcome.routes());
        response.setNodesExpanded(outcome.nodesExpanded());
        response.setNodesGenerated(outcome.nodesGenerated());
        response.setPeakNodesStored(outcome.peakNodesStored());
        response.setHeuristicPrecomputeMs(XgtHeuristic.precomputeMillis());
        return response;
    }

    public int searchWithoutTrace() {
        return search(false).distance();
    }

    private Outcome search(boolean traced) {
        String start = route.getStart();
        String goal = route.getEnd();

        int hStart = XgtHeuristic.h(goal, start);

        PriorityQueue<Entry> frontier = new PriorityQueue<>(
                Comparator.comparingInt(Entry::f).thenComparingInt(Entry::seq));
        Map<String, Integer> bestG = new HashMap<>();
        Map<String, String> parentMap = new HashMap<>();
        Set<String> closed = new HashSet<>();
        Map<String, Nodedtos> pushedAs = traced ? new HashMap<>() : null;
        Map<Integer, List<Nodedtos>> trace = traced ? new HashMap<>() : null;

        int seq = 0;
        frontier.add(new Entry(start, 0, hStart, hStart, seq++));
        bestG.put(start, 0);

        int step = 0;
        int nodesExpanded = 0;
        int nodesGenerated = 0;
        int peakNodesStored = 1;

        while (!frontier.isEmpty()) {
            Entry current = frontier.poll();
            if (closed.contains(current.city())) {
                continue; // stale entry: this city was already expanded with a better g
            }
            closed.add(current.city());
            nodesExpanded++;

            List<Nodedtos> generated = null;
            if (traced) {
                Nodedtos expanded = new Nodedtos(current.city(), current.g(), current.h(), current.f());
                expanded.setExpandedAt(step);
                if (pushedAs.containsKey(current.city())) {
                    pushedAs.get(current.city()).setExpandedAt(step);
                }
                generated = new ArrayList<>();
                generated.add(expanded);
                trace.put(step, generated);
            }

            if (current.city().equals(goal)) {
                break;
            }

            // GRAPH keeps each city's roads sorted by name, so the push order is fixed
            for (Map.Entry<String, Integer> road : RomaniaMap.GRAPH.get(current.city()).entrySet()) {
                nodesGenerated++;
                String neighbor = road.getKey();
                int g = current.g() + road.getValue();
                int h = XgtHeuristic.h(goal, neighbor);
                Nodedtos child = null;
                if (traced) {
                    child = new Nodedtos(neighbor, g, h, g + h);
                    generated.add(child);
                }

                if (!closed.contains(neighbor) && g < bestG.getOrDefault(neighbor, Integer.MAX_VALUE)) {
                    bestG.put(neighbor, g);
                    parentMap.put(neighbor, current.city());
                    if (traced) {
                        pushedAs.put(neighbor, child);
                    }
                    frontier.add(new Entry(neighbor, g, h, g + h, seq++));
                    peakNodesStored = Math.max(peakNodesStored, frontier.size() + closed.size());
                }
            }
            step++;
        }

        List<String> finalPath = reconstructPath(parentMap, goal);
        return new Outcome(finalPath, calculateTotalDistance(finalPath), bestG.size(),
                nodesExpanded, nodesGenerated, peakNodesStored, trace);
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
