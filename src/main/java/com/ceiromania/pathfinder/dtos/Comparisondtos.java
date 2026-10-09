package com.ceiromania.pathfinder.dtos;


import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Comparisondtos {

    private Measurementdtos bfs;
    private Measurementdtos astar;

    public Comparisondtos(Measurementdtos bfs, Measurementdtos astar) {
        this.bfs = bfs;
        this.astar = astar;
    }
}
