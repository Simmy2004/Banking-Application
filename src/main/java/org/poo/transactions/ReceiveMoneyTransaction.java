package org.poo.transactions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class ReceiveMoneyTransaction implements Transaction {

    private int timestamp;
    private String description;
    private String senderIban;
    private String receiverIban;
    private double amount;
    private String currency;
    public ReceiveMoneyTransaction(final int timestamp, final String description,
                                   final String senderIban, final String receiverIban,
                                   final double amount, final String currency) {
        this.timestamp = timestamp;
        this.description = description;
        this.senderIban = senderIban;
        this.receiverIban = receiverIban;
        this.amount = amount;
        this.currency = currency;
    }

    /**
     * Gets the details of the ReceiveMoney transaction type.
     * @return ObjectNode containing details about the current transaction.
     */
    @Override
    public ObjectNode getDetails() {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode details = mapper.createObjectNode();
        details.put("timestamp", timestamp);
        details.put("description", description);
        details.put("senderIBAN", senderIban);
        details.put("receiverIBAN", receiverIban);
        details.put("amount", amount + " " + currency);
        details.put("transferType", "received");

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
