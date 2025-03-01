package org.poo.accounts;

import lombok.Data;
import org.poo.solution.User;
import org.poo.weightedGraph.ExchangeGraph;

import java.util.ArrayList;

import static org.poo.solution.Constants.HIGHCAP;

@Data
public final class BusinessAccount extends Account {
    private ArrayList<User> managers;
    private ArrayList<User> employees;
    private double spendingLimit;
    private double depositLimit;
    private ArrayList<BusinessDetails> details;

    public BusinessAccount(final String currency, final User user) {
        super(currency, user);
        managers = new ArrayList<>();
        employees = new ArrayList<>();
        spendingLimit = HIGHCAP * ExchangeGraph.getInstance(null).getRateFromTo("RON", currency);
        depositLimit = HIGHCAP * ExchangeGraph.getInstance(null).getRateFromTo("RON", currency);
        details = new ArrayList<>();
    }
    @Override
    public String getType() {
        return "business";
    }
}
