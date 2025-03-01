package org.poo.accounts;

import lombok.Data;

@Data
public class BusinessDetails {
    private int timestamp;
    private String name;
    private double spent;
    private double deposited;
    private String commerciant;

    public BusinessDetails(final int timestamp, final String name, final double spent,
                           final double deposited, final String commerciant) {
        this.timestamp = timestamp;
        this.name = name;
        this.spent = spent;
        this.deposited = deposited;
        this.commerciant = commerciant;
    }
}
