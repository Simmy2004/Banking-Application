package org.poo.commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.accounts.Account;
import org.poo.solution.Database;
import org.poo.transactions.Transaction;

public class Report implements Command {
    private int startTime;
    private int endTime;
    private String iban;
    private int timestamp;
    private Database database;

    public Report(final int startTime, final int endTime, final String iban,
                  final int timestamp, final Database database) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.iban = iban;
        this.timestamp = timestamp;
        this.database = database;
    }

    /**
     * Executes the printReport command. Creates an ObjectNode containing information
     * about all the transactions of a user that happened between the given timestamps.
     * @return An ObjectNode containing information about all the transactions, or any
     * errors that occurred
     */
    @Override
    public ObjectNode execute() {
        Account account = database.getIbanToAccount().get(iban);
        ObjectMapper mapper = new ObjectMapper();
        if (account == null) {
            ObjectNode errorNode = mapper.createObjectNode();
            errorNode.put("command", "report");

            ObjectNode output = mapper.createObjectNode();
            output.put("timestamp", timestamp);
            output.put("description", "Account not found");

            errorNode.put("output", output);
            errorNode.put("timestamp", timestamp);

            return errorNode;
        }


        ObjectNode mainNode = mapper.createObjectNode();
        mainNode.put("command", "report");

        ObjectNode output = mapper.createObjectNode();
        output.put("IBAN", iban);
        output.put("balance", account.getBalance());
        output.put("currency", account.getCurrency());

        ArrayNode transactions = mapper.createArrayNode();
        for (Transaction transaction : account.getTransactions()) {
            if (transaction.getTimestamp() >= startTime && transaction.getTimestamp() <= endTime) {
                ObjectNode transactionNode = transaction.getDetails();
                transactions.add(transactionNode);
            }

        }

        output.put("transactions", transactions);
        mainNode.put("output", output);
        mainNode.put("timestamp", timestamp);

        return mainNode;
    }
}
