package org.poo.transactions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.text.DecimalFormat;
import java.util.List;

public final class CustomSplitTransaction implements Transaction {

    private int timestamp;
    private String currency;
    private double totalAmount;
    private List<String> accounts;
    private String errorAccount;
    private List<Double> amounts;

    public CustomSplitTransaction(final int timestamp, final String currency,
                                  final double totalAmount, final List<Double> amounts,
                                  final List<String> accounts, final String errorAccount) {
        this.timestamp = timestamp;
        this.currency = currency;
        this.totalAmount = totalAmount;
        this.accounts = accounts;
        this.errorAccount = errorAccount;
        this.amounts = amounts;
    }
    @Override
    public ObjectNode getDetails() {
        DecimalFormat df = new DecimalFormat("#.00");
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode details = mapper.createObjectNode();
        details.put("timestamp", timestamp);

        details.put("description", "Split payment of " + df.format(totalAmount) + " " + currency);
        details.put("splitPaymentType", "custom");
        details.put("currency", currency);
        ArrayNode amountArray = mapper.createArrayNode();

        for (double amount : amounts) {
            amountArray.add(amount);
        }
        details.put("amountForUsers", amountArray);

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

    @Override
    public int getTimestamp() {
        return timestamp;
    }

    @Override
    public boolean isSpending() {
        return false;
    }
}
