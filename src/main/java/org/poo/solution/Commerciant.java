package org.poo.solution;

import lombok.Data;
import org.poo.fileio.CommerciantInput;
import org.poo.cashBackStrategies.CashBackStrategy;
import org.poo.cashBackStrategies.NumberOfTransactionsStrategy;
import org.poo.cashBackStrategies.SpendingThresholdStrategy;

@Data
public class Commerciant {
    private String name;
    private int id;
    private String account;
    private String type;
    private String strategy;
    private CashBackStrategy cashbackStrategy;

    public Commerciant(final CommerciantInput input) {
        this.name = input.getCommerciant();
        this.id = input.getId();
        this.account = input.getAccount();
        this.type = input.getType();
        this.strategy = input.getCashbackStrategy();
        switch (input.getCashbackStrategy()) {
            case "spendingThreshold" -> {
                this.cashbackStrategy = new SpendingThresholdStrategy();
            }
            case "nrOfTransactions" -> {
                this.cashbackStrategy = new NumberOfTransactionsStrategy();
            }
            default -> this.cashbackStrategy = null;
        }
    }
}
