package org.poo.accounts;

import org.poo.transactions.Transaction;

public interface SplitObserver {
    /**
     * Observer design pattern for notifying split payments that should be either
     * accepted or rejected.
     * @param transaction The transaction that will be added to all the clients
     * @param amount The amount that will be subtracted from all clients.
     * @param error Error if the payment can't be made.
     */
    void update(Transaction transaction, double amount, String error);
}
