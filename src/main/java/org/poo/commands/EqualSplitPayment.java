package org.poo.commands;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.solution.Database;
import org.poo.splitPayments.SplitReport;

import java.util.ArrayList;
import java.util.List;

public class EqualSplitPayment implements Command {
    private List<String> ibans;
    private double amountToSplit;
    private String currency;
    private int timestamp;
    private Database database;

    public EqualSplitPayment(final List<String> ibans, final double amountToSplit,
                             final String currency, final int timestamp,
                             final Database database) {
        this.ibans = ibans;
        this.amountToSplit = amountToSplit;
        this.currency = currency;
        this.timestamp = timestamp;
        this.database = database;
    }

    /**
     * Executes the SplitPayment command. Checks for any accounts involved in the payment that
     * have no funds, and in case of not finding one, the translated amount to each
     * account currency will be subtracted. Adds the transaction to each
     * user history.
     * @return This command always returns a null ObjectNode because no information
     * is requested about this process.
     */
    @Override
    public ObjectNode execute() {
        List<Double> amounts = new ArrayList<>();
        for (int i = 0; i < ibans.size(); i++) {
            amounts.add(amountToSplit / (double) ibans.size());
        }
        database.getSplits().addLast(new SplitReport(timestamp, ibans, amounts, "equal",
                currency, amountToSplit, database));

        return null;
    }

}
