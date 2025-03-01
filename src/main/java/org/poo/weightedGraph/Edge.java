package org.poo.weightedGraph;

import lombok.Data;

@Data
public final class Edge {
    private String currency;
    private double rate;

    public Edge(final double rate, final String currency) {
        this.rate = rate;
        this.currency = currency;
    }
}
