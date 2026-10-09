package com.ceiromania.pathfinder.services;

import com.ceiromania.pathfinder.data.RomaniaMap;
import com.ceiromania.pathfinder.dtos.Responsedtos;
import com.ceiromania.pathfinder.dtos.Routedtos;

import java.util.*;

/**
 * Breadth-first search, goal test when the goal is generated. Counters use the same rules as
 * AStarSearch: nodesExpanded = cities taken off the frontier, nodesGenerated = neighbours looked at,
 * peakNodesStored = largest (frontier entries + expanded cities) at any moment.
 */
public class BfsSearch {

    private final Routedtos route;

    public BfsSearch(Routedtos route) {
        this.route = route;
    }

    /** routes and expanded are null when the search ran without its trace. */
    private record Outcome(List<String> path, int distance, int totalNodes, int nodesExpanded,
                           int nodesGenerated, int peakNodesStored,
                           Map<Integer, List<String>> routes, List<String> expanded) {
    }

    public Responsedtos findRoute() {
        Outcome outcome = search(true);
        if (outcome == null) {
            return null;
        }

        Responsedtos response = new Responsedtos(
                outcome.routes(),
                outcome.totalNodes(),
                outcome.distance(),
                outcome.path()
        );
        response.setExpanded(outcome.expanded());
        response.setNodesExpanded(outcome.nodesExpanded());
        response.setNodesGenerated(outcome.nodesGenerated());
        response.setPeakNodesStored(outcome.peakNodesStored());
        return response;
    }

    /** The search SearchBenchmark times: no trace, result reduced to the path cost. */
    public int searchWithoutTrace() {
        return search(false).distance();
    }

    private Outcome search(boolean traced) {
        String start = route.getStart();
        String goal = route.getEnd();

        Map<Integer, List<String>> result = traced ? new HashMap<>() : null;
        List<String> expanded = traced ? new ArrayList<>() : null;

        if (start.equals(goal)) {
            List<String> path = new ArrayList<>(List.of(start));
            if (traced) {
                result.put(0, new ArrayList<>(path));
            }
            return new Outcome(path, 0, 1, 0, 0, 1, result, expanded);
        }

        Queue<String> queue = new ArrayDeque<>();
        Set<String> visited = new HashSet<>();
        Map<String, String> parentMap = new HashMap<>();
        List<String> generated = traced ? new ArrayList<>() : null;

        queue.add(start);
        visited.add(start);

        int step = 0;
        int nodesExpanded = 0;
        int nodesGenerated = 0;
        int peakNodesStored = 1;

        while (!queue.isEmpty()) {
            String currentCity = queue.poll();
            nodesExpanded++;
            if (traced) {
                expanded.add(currentCity);
            }

            for (String neighbor : RomaniaMap.GRAPH.getOrDefault(currentCity, Collections.emptyMap()).keySet()) {
                nodesGenerated++;
                if (neighbor.equals(goal)) {
                    parentMap.put(neighbor, currentCity);
                    if (traced) {
                        generated.add(neighbor);
                        result.put(step, new ArrayList<>(generated));
                    }

                    List<String> finalPath = reconstructPath(parentMap, goal);
                    // totalNodes = distinct cities reached, goal included (same as A*)
                    return new Outcome(finalPath, calculateTotalDistance(finalPath), visited.size() + 1,
                            nodesExpanded, nodesGenerated, peakNodesStored, result, expanded);
                } else if (!visited.contains(neighbor)) {
                    visited.add(neighbor);
                    parentMap.put(neighbor, currentCity);
                    queue.add(neighbor);
                    if (traced) {
                        generated.add(neighbor);
                    }
                    peakNodesStored = Math.max(peakNodesStored, queue.size() + nodesExpanded);
                }
            }

            if (traced) {
                result.put(step, new ArrayList<>(generated));
                generated.clear();
            }
            step++;
        }

        return null;
    }

    public List<String> reconstructPath(Map<String, String> parentMap, String end) {
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
            String from = path.get(i);
            String to = path.get(i + 1);
            total += RomaniaMap.GRAPH.get(from).get(to);
        }
        return total;
    }
}
