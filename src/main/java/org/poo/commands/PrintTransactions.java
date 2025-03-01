package org.poo.commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.solution.User;
import org.poo.transactions.Transaction;

public class PrintTransactions implements Command {
    private User user;
    private int timestamp;
    public PrintTransactions(final User user, final int timestamp) {
        this.user = user;
        this.timestamp = timestamp;
    }

    /**
     * Executes the printTransactions command. Prints every transaction in the user's
     * history list.
     * @return An ObjectNode containing information about all the transactions in user's
     * history.
     */
    @Override
    public ObjectNode execute() {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode mainNode = mapper.createObjectNode();
        mainNode.put("command", "printTransactions");

        ArrayNode transactionsNode = mapper.createArrayNode();
        for (Transaction transaction : user.getTransactions()) {
            transactionsNode.add(transaction.getDetails());
        }

        mainNode.put("output", transactionsNode);
        mainNode.put("timestamp", timestamp);
        return mainNode;
    }
}
