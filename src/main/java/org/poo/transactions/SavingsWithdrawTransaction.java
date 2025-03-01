package org.poo.transactions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public final class SavingsWithdrawTransaction implements Transaction {
    private int timestamp;
    private String classicIban;
    private String savingsIban;
    private double amount;

    public SavingsWithdrawTransaction(final int timestamp, final String classicIban,
                                      final String savingsIban, final double amount) {
        this.timestamp = timestamp;
        this.classicIban = classicIban;
        this.savingsIban = savingsIban;
        this.amount = amount;
    }
    @Override
    public ObjectNode getDetails() {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode details = mapper.createObjectNode();
        details.put("amount", amount);
        details.put("classicAccountIBAN", classicIban);
        details.put("description", "Savings withdrawal");
        details.put("savingsAccountIBAN", savingsIban);
        details.put("timestamp", timestamp);
        return details;
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
