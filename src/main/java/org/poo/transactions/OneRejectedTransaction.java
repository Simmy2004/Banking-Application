package org.poo.transactions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.text.DecimalFormat;
import java.util.List;

public final class OneRejectedTransaction implements Transaction {
    private int timestamp;
    private String type;
    private List<String> ibans;
    private List<Double> amounts;
    private String currency;

    public OneRejectedTransaction(final String type, final List<Double> amounts,
                                  final String currency, final List<String> ibans,
                                  final int timestamp) {
        this.timestamp = timestamp;
        this.type = type;
        this.amounts = amounts;
        this.currency = currency;
        this.ibans = ibans;
    }

    @Override
    public ObjectNode getDetails() {
        double totalAmount = 0;
        DecimalFormat df = new DecimalFormat("#.00");

        ObjectMapper mapper = new ObjectMapper();
        ArrayNode amountsNode = mapper.createArrayNode();

        for (double amount : amounts) {
            totalAmount += amount;
            amountsNode.add(amount);
        }


        ObjectNode details = mapper.createObjectNode();
        details.put("timestamp", timestamp);
        details.put("description", "Split payment of " + df.format(totalAmount) + " " + currency);
        details.put("splitPaymentType", type);
        details.put("currency", currency);

        details.put("amountForUsers", amountsNode);

        ArrayNode ibansNode = mapper.createArrayNode();
        for (String iban : ibans) {
            ibansNode.add(iban);
        }
        details.put("involvedAccounts", ibansNode);
        details.put("error", "One user rejected the payment.");
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
