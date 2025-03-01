package org.poo.transactions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public final class PlanUpgradedTransaction implements Transaction {
    private int timestamp;
    private String plan;
    private String iban;

    public PlanUpgradedTransaction(final int timestamp, final String plan, final String iban) {
        this.timestamp = timestamp;
        this.plan = plan;
        this.iban = iban;
    }

    @Override
    public ObjectNode getDetails() {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode details = mapper.createObjectNode();
        details.put("timestamp", timestamp);
        details.put("description", "Upgrade plan");
        details.put("accountIBAN", iban);
        details.put("newPlanType", plan);
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
