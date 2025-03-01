package org.poo.transactions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public final class NoClassicAccountTransaction implements Transaction {
    private int timestamp;

    public NoClassicAccountTransaction(final int timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public ObjectNode getDetails() {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode details = mapper.createObjectNode();
        details.put("description", "You do not have a classic account.");
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
