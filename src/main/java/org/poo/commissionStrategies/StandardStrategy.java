package org.poo.commissionStrategies;

import org.poo.accounts.Account;

import static org.poo.solution.Constants.STANDARD_RATE;

public final class StandardStrategy implements CommissionStrategy {
    @Override
    public void payCommission(final Account account, final double amount) {
        account.setBalance(account.getBalance() - amount * STANDARD_RATE);
    }

    @Override
    public double getCommission(final Account account, final double amount) {
        return amount * STANDARD_RATE;
    }
}
