package org.poo.transactions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.text.DecimalFormat;
import java.util.List;

public class EqualSplitTransaction implements Transaction {
    private int timestamp;
    private String currency;
    private double amount;
    private List<String> accounts;
    private String errorAccount;

    public EqualSplitTransaction(final int timestamp, final String currency, final double amount,
                                 final List<String> accounts, final String errorAccount) {
        this.timestamp = timestamp;
        this.currency = currency;
        this.amount = amount;
        this.accounts = accounts;
        this.errorAccount = errorAccount;
    }

    /**
     * Gets the details of the Split transaction type.
     * @return ObjectNode containing details about the current transaction.
     */
    @Override
    public ObjectNode getDetails() {
        ObjectMapper mapper = new ObjectMapper();
        DecimalFormat df = new DecimalFormat("#.00");
        ObjectNode details = mapper.createObjectNode();
        details.put("timestamp", timestamp);
        details.put("description", "Split payment of " + df.format(amount) + " " + currency);
        details.put("splitPaymentType", "equal");
        details.put("currency", currency);
        details.put("amount", amount / accounts.size());

        ArrayNode accountsNode = mapper.createArrayNode();
        for (String account : accounts) {
            accountsNode.add(account);
        }
        details.put("involvedAccounts", accountsNode);

        if (errorAccount != null) {
            details.put("error", "Account " + errorAccount
                    + " has insufficient funds for a split payment.");
        }
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
