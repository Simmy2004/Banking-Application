package org.poo.commands;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.solution.Database;
import org.poo.splitPayments.SplitReport;

import java.util.List;

public final class CustomSplitPayment implements Command {
    private List<String> ibans;
    private double totalAmount;
    private List<Double> amounts;
    private String currency;
    private int timestamp;
    private Database database;

    public CustomSplitPayment(final List<String> ibans, final double totalAmount,
                              final List<Double> amounts, final String currency,
                              final int timestamp, final Database database) {
        this.ibans = ibans;
        this.totalAmount = totalAmount;
        this.amounts = amounts;
        this.currency = currency;
        this.timestamp = timestamp;
        this.database = database;
    }
    @Override
    public ObjectNode execute() {
        database.getSplits().addLast(new SplitReport(timestamp, ibans, amounts, "custom",
                currency, totalAmount, database));

        return null;
    }
}
