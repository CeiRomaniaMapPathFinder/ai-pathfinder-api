package com.ceiromania.pathfinder.dtos;


import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

public class Responsedtos {

    @Setter
    @Getter
    private Map<Integer,List<String>> routes;

    @Setter
    @Getter
    private int totalNodes;

    @Setter
    @Getter
    private int distance;

    @Setter
    @Getter
    private List<String> path;

    @Setter
    @Getter
    private List<String> expanded;

    @Setter
    @Getter
    private int nodesExpanded;

    @Setter
    @Getter
    private int nodesGenerated;

    @Setter
    @Getter
    private int peakNodesStored;

    public Responsedtos(Map<Integer,List<String>> routes, int totalNodes, int distance, List<String> path) {
        this.routes = routes;
        this.totalNodes = totalNodes;
        this.distance = distance;
        this.path = path;
    }
}