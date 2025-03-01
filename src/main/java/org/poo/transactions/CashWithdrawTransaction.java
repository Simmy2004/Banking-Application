package org.poo.transactions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public final class CashWithdrawTransaction implements Transaction {
    private int timestamp;
    private double amount;

    public CashWithdrawTransaction(final int timestamp, final double amount) {
        this.timestamp = timestamp;
        this.amount = amount;
    }
    @Override
    public ObjectNode getDetails() {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode mainNode = mapper.createObjectNode();
        mainNode.put("amount", amount);
        mainNode.put("description", "Cash withdrawal of " + amount);
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
