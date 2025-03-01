package org.poo.commissionStrategies;

import org.poo.accounts.Account;
import org.poo.weightedGraph.ExchangeGraph;

import static org.poo.solution.Constants.HIGHCAP;
import static org.poo.solution.Constants.SILVER_RATE;

public final class SilverStrategy implements CommissionStrategy {
    @Override
    public void payCommission(final Account account, final double amount) {
        double amountInRon = amount * ExchangeGraph.
                getInstance(null).getRateFromTo(account.getCurrency(), "RON");
        if (amountInRon > HIGHCAP) {
            account.setBalance(account.getBalance() - amount * SILVER_RATE);
        }
    }

    @Override
    public double getCommission(final Account account, final double amount) {
        double amountInRon = amount * ExchangeGraph.
                getInstance(null).getRateFromTo(account.getCurrency(), "RON");
        if (amountInRon > HIGHCAP) {
            return amount * SILVER_RATE;
        }
        return 0;
    }
}
