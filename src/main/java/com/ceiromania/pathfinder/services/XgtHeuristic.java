package com.ceiromania.pathfinder.services;

import com.ceiromania.pathfinder.data.RomaniaMap;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public final class XgtHeuristic {

    private static final double BETA = 0.02;
    private static final double GAMMA = 0.01;
    private static final double TAU = 1.5;
    private static final double INITIAL = 1e-3;
    private static final double CLAMP = 1e-30;
    private static final double TOLERANCE = 1e-12;
    private static final int MAX_ITERATIONS = 100_000;

    private static final Map<String, Map<String, Integer>> ROADS = new TreeMap<>();
    private static final Map<String, Map<String, Integer>> TABLES = new HashMap<>();
    private static final double PRECOMPUTE_MILLIS;

    static {
        long startTime = System.nanoTime();
        for (Map.Entry<String, Map<String, Integer>> city : RomaniaMap.GRAPH.entrySet()) {
            ROADS.put(city.getKey(), new TreeMap<>(city.getValue()));
        }
        for (String goal : ROADS.keySet()) {
            TABLES.put(goal, compute(goal));
        }
        PRECOMPUTE_MILLIS = (System.nanoTime() - startTime) / 1_000_000.0;
    }

    private XgtHeuristic() {
    }

    public static Map<String, Integer> table(String goal) {
        return TABLES.get(goal);
    }

    public static int h(String goal, String city) {
        return TABLES.get(goal).get(city);
    }

    public static double precomputeMillis() {
        return PRECOMPUTE_MILLIS;
    }

    private static Map<String, Integer> compute(String goal) {
        Map<String, Double> xgt = new HashMap<>();
        for (String city : ROADS.keySet()) {
            xgt.put(city, city.equals(goal) ? 1.0 : INITIAL);
        }

        // Jacobi iteration: every new value is computed from the previous table only.
        for (int iteration = 1; ; iteration++) {
            Map<String, Double> next = new HashMap<>();
            double delta = 0.0;
            for (String city : ROADS.keySet()) {
                double value = city.equals(goal) ? 1.0 : propagate(ROADS.get(city), xgt);
                next.put(city, value);
                delta = Math.max(delta, Math.abs(value - xgt.get(city)));
            }
            xgt = next;
            if (delta < TOLERANCE) {
                break;
            }
            if (iteration >= MAX_ITERATIONS) {
                throw new IllegalStateException("xGT did not converge for goal " + goal);
            }
        }

        Map<String, Integer> h = new HashMap<>();
        for (Map.Entry<String, Double> city : xgt.entrySet()) {
            int value = (int) Math.floor(-Math.log(city.getValue()) / GAMMA + 0.5);
            h.put(city.getKey(), Math.max(value, 0));
        }
        h.put(goal, 0);
        return h;
    }

    private static double propagate(Map<String, Integer> roads, Map<String, Double> xgt) {
        double sumPreference = 0.0;
        for (Map.Entry<String, Integer> road : roads.entrySet()) {
            sumPreference += preference(road.getValue(), xgt.get(road.getKey()));
        }
        double value = 0.0;
        for (Map.Entry<String, Integer> road : roads.entrySet()) {
            int cost = road.getValue();
            double receiver = xgt.get(road.getKey());
            double pick = preference(cost, receiver) / sumPreference;
            double complete = Math.exp(-GAMMA * cost);
            value += pick * complete * receiver;
        }
        return value;
    }

    private static double preference(int cost, double receiver) {
        return Math.exp(-BETA * cost) * Math.pow(Math.max(receiver, CLAMP), TAU);
    }
}
