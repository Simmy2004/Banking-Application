package org.poo.transactions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public final class SamePlanTransaction implements Transaction {
    private int timestamp;
    private String plan;

    public SamePlanTransaction(final int timestamp, final String plan) {
        this.timestamp = timestamp;
        this.plan = plan;
    }

    @Override
    public ObjectNode getDetails() {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode details = mapper.createObjectNode();
        details.put("description", "The user already has the " + plan + " plan.");
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
