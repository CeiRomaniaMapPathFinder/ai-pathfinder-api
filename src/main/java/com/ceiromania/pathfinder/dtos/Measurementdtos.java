package com.ceiromania.pathfinder.dtos;


import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Measurementdtos {

    private double runtime;
    private double memoryUsageKb;
    private int timedRuns;

    public Measurementdtos(double runtime, double memoryUsageKb, int timedRuns) {
        this.runtime = runtime;
        this.memoryUsageKb = memoryUsageKb;
        this.timedRuns = timedRuns;
    }
}
