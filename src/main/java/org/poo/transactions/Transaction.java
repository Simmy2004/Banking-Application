package org.poo.transactions;

import com.fasterxml.jackson.databind.node.ObjectNode;

public interface Transaction {
    /**
     * Method made for a Command Design Pattern.
     * @return Details about specific transaction type.
     */
    ObjectNode getDetails();

    /**
     * Getter for the timestamp.
     * @return Integer representing timestamp.
     */
    int getTimestamp();

    /**
     * Method used to see if any class that implements transaction si also a spending.
     * @return True if the transaction also implements the spending interface.
     */
    boolean isSpending();
}
