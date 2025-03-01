package org.poo.transactions;


public interface Spending {
    double amount = 0;
    /**
     * Every Spending type must have a getter for the commerciants
     * where the money was paid.
     * @return String representing commerciant where a payment was made.
     */
    String getCommerciant();

    /**
     * Every Spending type must have a getter for the amount paid at a
     * specific commerciant
     * @return Double representing amount paid to the respective commerciant.
     */
    double getAmount();
}
