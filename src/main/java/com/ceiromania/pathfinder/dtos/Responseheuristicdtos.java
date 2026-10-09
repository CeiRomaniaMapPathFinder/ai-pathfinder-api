package com.ceiromania.pathfinder.dtos;


import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Setter
@Getter
public class Responseheuristicdtos {

    private Map<Integer,List<Nodedtos>> routes;
    private int totalNodes;
    private int distance;
    private List<String> path;
    private int nodesExpanded;
    private int nodesGenerated;
    private int peakNodesStored;
    private double heuristicPrecomputeMs;

    public Responseheuristicdtos(int totalNodes, int distance, List<String> path) {
        this.path = path;
        this.totalNodes = totalNodes;
        this.distance = distance;
    }
}