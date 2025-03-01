package org.poo.commissionStrategies;

import org.poo.accounts.Account;

/**
 * Strategy design pattern for selecting different commission strategies for paying.
 */
public interface CommissionStrategy {
    /**
     * Function to pay the commission for a transaction, if it exists.
     * @param account Account where the funds will be subtracted.
     * @param amount Amount that will help calculate the commission.
     */
    void payCommission(Account account, double amount);

    /**
     * Gets the calculated commission.
     * @param account Account where the funds will be subtracted.
     * @param amount Amount that will help calculate the commission.
     * @return Commission calculated.
     */
    double getCommission(Account account, double amount);
}
