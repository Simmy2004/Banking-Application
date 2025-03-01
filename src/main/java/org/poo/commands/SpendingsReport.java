package org.poo.commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.accounts.Account;
import org.poo.solution.Database;
import org.poo.transactions.Spending;
import org.poo.transactions.Transaction;

import java.util.*;


public class SpendingsReport implements Command {
    private int startTime;
    private int endTime;
    private String iban;
    private int timestamp;
    private Database database;

    public SpendingsReport(final int startTime, final int endTime, final String iban,
                           final int timestamp, final Database database) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.iban = iban;
        this.timestamp = timestamp;
        this.database = database;
    }

    /**
     * Executes the spendingReport command. Creates an output ObjectNode containing all the
     * spendings found in specific user's history.
     * @return ObjectNode containing requested information.
     */
    @Override
    public ObjectNode execute() {
        Account owner = database.getIbanToAccount().get(iban);
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode mainNode = mapper.createObjectNode();
        mainNode.put("command", "spendingsReport");

        if (owner == null) {
            ObjectNode errorNode = mapper.createObjectNode();
            errorNode.put("timestamp", timestamp);
            errorNode.put("description", "Account not found");

            mainNode.put("output", errorNode);
            mainNode.put("timestamp", timestamp);
            return mainNode;
        }

        if (owner.getType().equals("savings")) {
            ObjectNode errorNode = mapper.createObjectNode();
            errorNode.put("error", "This kind of report is not supported for a saving account");
            mainNode.put("output", errorNode);
            mainNode.put("timestamp", timestamp);

            return mainNode;
        }

        ObjectNode result = mapper.createObjectNode();
        result.put("IBAN", iban);
        result.put("balance", owner.getBalance());
        result.put("currency", owner.getCurrency());

        ArrayNode transactionNode = mapper.createArrayNode();
        ArrayNode commerciantNode = mapper.createArrayNode();

        Map<String, Double> commerciantPayments = new HashMap<>();
        for (Transaction transaction : owner.getTransactions()) {
            if (transaction.getTimestamp() >= startTime && transaction.getTimestamp() <= endTime
            && transaction.isSpending()) {
                transactionNode.add(transaction.getDetails());
                Spending spending = (Spending) transaction;

                if (!commerciantPayments.containsKey(spending.getCommerciant())) {
                    commerciantPayments.put(spending.getCommerciant(), spending.getAmount());
                } else {
                    double newAmount = commerciantPayments.get(
                            spending.getCommerciant()) + spending.getAmount();
                    commerciantPayments.put(spending.getCommerciant(), newAmount);
                }

            }
        }
        result.put("transactions", transactionNode);
        commerciantPayments = sortCommerciant(commerciantPayments);

        for (String commerciant : commerciantPayments.keySet()) {
            ObjectNode node = mapper.createObjectNode();
            node.put("commerciant", commerciant);
            node.put("total", commerciantPayments.get(commerciant));

            commerciantNode.add(node);
        }

        result.put("commerciants", commerciantNode);
        mainNode.put("output", result);
        mainNode.put("timestamp", timestamp);
        return mainNode;
    }

    /**
     * Helper function that sorts the Map that associates a commerciant name to
     * the total amount paid to him. The sort is made on the first letter of the name
     * @param commerciantPayments All the commerciants with the amount paid associated
     * @return The sorted map
     */
    private Map<String, Double> sortCommerciant(final Map<String, Double> commerciantPayments) {
        Map<String, Double> sortedCommerciant = new LinkedHashMap<>();
        ArrayList<String> commerciants = new ArrayList<>(commerciantPayments.keySet());
        Collections.sort(commerciants);

        for (String commerciant : commerciants) {
            sortedCommerciant.put(commerciant, commerciantPayments.get(commerciant));
        }

        return sortedCommerciant;
    }
}
