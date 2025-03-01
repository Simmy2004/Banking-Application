package org.poo.transactions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class InsufficientFunds implements Transaction {
    private int timestamp;

    public InsufficientFunds(final int timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * Gets the details of the InsufficientFunds transaction type.
     * @return ObjectNode containing details about the current transaction.
     */
    @Override
    public ObjectNode getDetails() {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode details = mapper.createObjectNode();
        details.put("timestamp", timestamp);
        details.put("description", "Insufficient funds");
        return details;
    }

    /**
     * Getter for timestamp
     * @return Timestamp
     */
    @Override
    public int getTimestamp() {
        return timestamp;
    }

    /**
     * Used for checking transaction type, if it is seen as a spending.
     * @return Boolean representing if transaction is a spending.
     */
    @Override
    public boolean isSpending() {
        return false;
    }
}
