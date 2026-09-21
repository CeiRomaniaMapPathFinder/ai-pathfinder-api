package com.ceiromania.pathfinder.dtos;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Nodedtos {

    private String town;
    private int gN;
    private int hN;
    private int fN;
    private int expandedAt = -1; 

    public Nodedtos(String town, int gN, int hN, int fN) {
        this.town = town;
        this.gN = gN;
        this.hN = hN;
        this.fN = fN;
    }
}