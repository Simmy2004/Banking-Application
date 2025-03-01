package org.poo.cashBackStrategies;

import org.poo.accounts.Account;
import org.poo.solution.Commerciant;

public interface CashBackStrategy {
    /**
     * Strategy design pattern, implements different cashback strategies for commerciant payments
     * @param account The account where the cashback will be transferred.
     * @param commerciant Commerciant that will give the cashback.
     * @param amount Amount from where the cashback will be calculated.
     */
    void getCashBack(Account account, Commerciant commerciant, double amount);
}
