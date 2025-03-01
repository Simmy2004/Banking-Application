package org.poo.transactions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class CardPayment implements Transaction, Spending {
    private int timestamp;
    private double amount;
    private String commerciant;

    public CardPayment(final int timestamp, final double amount, final String commerciant) {
        this.timestamp = timestamp;
        this.amount = amount;
        this.commerciant = commerciant;
    }

    /**
     * Gives the details for the CardPayment Transaction type.
     * @return ObjectNode containing details for the CardPayment Transaction type.
     */
    @Override
    public ObjectNode getDetails() {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode details = mapper.createObjectNode();

        details.put("timestamp", timestamp);
        details.put("description", "Card payment");
        details.put("amount", amount);
        details.put("commerciant", commerciant);

        return details;
    }

    /**
     * Getter for timestamp field
     * @return Int representing the timestamp
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
        return true;
    }

    /**
     * Getter for commerciant
     * @return String representing the commerciant
     */
    @Override
    public String getCommerciant() {
        return commerciant;
    }
    /**
     * Getter for amount
     * @return double variable representing amount.
     */
    @Override
    public double getAmount() {
        return amount;
    }
}
