package com.ceiromania.pathfinder.services;

import com.ceiromania.pathfinder.data.RomaniaMap;
import com.ceiromania.pathfinder.dtos.Responsedtos;
import com.ceiromania.pathfinder.dtos.Routedtos;

import java.util.*;

public class BfsSearch {

    private final Routedtos route;

    public BfsSearch(Routedtos route) {
        this.route = route;
    }

    public Responsedtos findRoute() {
        Queue<String> queue = new ArrayDeque<>();
        Set<String> visited = new HashSet<>();
        List<String> path = new ArrayList<>();
        Map<Integer,List<String>> result = new HashMap<>();
        Map<String, String> parentMap = new HashMap<>();

        int step = 0;
        long startTime = System.nanoTime();

        queue.add(route.getStart());
        visited.add(route.getStart());

        while (!queue.isEmpty()) {
            String currentCity = queue.poll();

            for (String neighbor : RomaniaMap.GRAPH.getOrDefault(currentCity, Collections.emptyMap()).keySet()) {
                if (neighbor.equals(route.getEnd())) {
                    parentMap.put(neighbor, currentCity);
                    path.add(neighbor);
                    result.put(step, new ArrayList<>(path));

                    int totalNodes = visited.size();

                    List<String> finalPath = reconstructPath(parentMap, route.getEnd());
                    int totalDistance = calculateTotalDistance(finalPath);

                    long endTime = System.nanoTime();
                    double durationInMs = (endTime - startTime) / 1_000_000.0;

                    return new Responsedtos(
                            result,
                            totalNodes,
                            totalDistance,
                            finalPath,
                            durationInMs
                    );
                } else if (!visited.contains(neighbor)) {
                    visited.add(neighbor);
                    path.add(neighbor);
                    parentMap.put(neighbor, currentCity);
                    queue.add(neighbor);
                }
            }

            result.put(step, new ArrayList<>(path));
            path.clear();
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