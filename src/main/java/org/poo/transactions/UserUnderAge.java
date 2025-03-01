package org.poo.transactions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public final class UserUnderAge implements Transaction {

    private int timestamp;
    public UserUnderAge(final int timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public ObjectNode getDetails() {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode node = mapper.createObjectNode();
        node.put("timestamp", timestamp);
        node.put("description", "You don't have the minimum age required.");
        return node;
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
