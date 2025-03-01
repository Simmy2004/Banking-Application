package org.poo.transactions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public final class AddInterestTransaction implements Transaction {
    private double amount;
    private String currency;
    private int timestamp;

    public AddInterestTransaction(final double amount,
                                  final String currency, final int timestamp) {
        this.amount = amount;
        this.currency = currency;
        this.timestamp = timestamp;
    }
    @Override
    public ObjectNode getDetails() {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode mainNode = mapper.createObjectNode();
        mainNode.put("amount", amount);
        mainNode.put("currency", currency);
        mainNode.put("description", "Interest rate income");
        mainNode.put("timestamp", timestamp);
        return mainNode;
    }

    @Override
    public int getTimestamp() {
        return timestamp;
    }

    @Override
    public boolean isSpending() {
        return false;
    }
}
