package com.ceiromania.pathfinder.dtos;


import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Setter
@Getter
public class Responseheuristicdtos {

    private Map<Integer,List<Nodedtos>> route;
    private int totalNodes;
    private int distance;
    private List<String> path;
    private double runtime;

    public Responseheuristicdtos(int totalNodes, int distance, double runtime , List<String> path) {
        this.path = path;
        this.totalNodes = totalNodes;
        this.distance = distance;
        this.runtime = runtime;
    }
}