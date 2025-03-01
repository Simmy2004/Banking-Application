package org.poo.commissionStrategies;

import org.poo.accounts.Account;

public final class GoldStudentStrategy implements CommissionStrategy {
    @Override
    public void payCommission(final Account account, final double amount) {
        return;
    }

    @Override
    public double getCommission(final Account account, final double amount) {
        return 0;
    }
}
