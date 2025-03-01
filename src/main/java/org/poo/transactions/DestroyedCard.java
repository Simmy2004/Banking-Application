package org.poo.transactions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class DestroyedCard implements Transaction {
    private int timestamp;
    private String cardNumber;
    private String emailHolder;
    private String iban;

    public DestroyedCard(final int timestamp, final String cardNumber,
                         final String emailHolder, final String iban) {
        this.timestamp = timestamp;
        this.cardNumber = cardNumber;
        this.emailHolder = emailHolder;
        this.iban = iban;
    }

    /**
     * Gets the details of the DestroyCard transaction type.
     * @return ObjectNode containing details about the current transaction.
     */
    @Override
    public ObjectNode getDetails() {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode details = mapper.createObjectNode();

        details.put("timestamp", timestamp);
        details.put("description", "The card has been destroyed");
        details.put("card", cardNumber);
        details.put("cardHolder", emailHolder);
        details.put("account", iban);

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
