package org.poo.commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.accounts.Account;
import org.poo.solution.Database;
import org.poo.solution.User;
import org.poo.transactions.CashWithdrawTransaction;
import org.poo.transactions.InsufficientFunds;
import org.poo.weightedGraph.ExchangeGraph;

public final class CashWithdraw implements Command {
    private String cardNumber;
    private Database database;
    private double amount;
    private String email;
    private String location;
    private int timestamp;

    public CashWithdraw(final String cardNumber, final double amount, final String email,
                        final String location, final int timestamp, final Database database) {
        this.cardNumber = cardNumber;
        this.amount = amount;
        this.email = email;
        this.location = location;
        this.timestamp = timestamp;
        this.database = database;
    }
    @Override
    public ObjectNode execute() {
        Account account = database.getCardNumberToAccount().get(cardNumber);
        User user = database.getMailToUser().get(email);
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode mainNode = mapper.createObjectNode();

        if (user == null) {
            mainNode.put("command", "cashWithdrawal");
            ObjectNode output = mapper.createObjectNode();
            output.put("description", "User not found");
            output.put("timestamp", timestamp);
            mainNode.put("output", output);
            mainNode.put("timestamp", timestamp);
            return mainNode;
        }
        if (account == null || user != account.getOwner()) {
            mainNode.put("command", "cashWithdrawal");
            ObjectNode output = mapper.createObjectNode();
            output.put("description", "Card not found");
            output.put("timestamp", timestamp);
            mainNode.put("output", output);
            mainNode.put("timestamp", timestamp);
            return mainNode;
        }

        double convertedAmount = amount * ExchangeGraph.getInstance(null).
                getRateFromTo("RON", account.getCurrency());

        if (convertedAmount > account.getBalance()) {
            user.getTransactions().addLast(new InsufficientFunds(timestamp));
            return null;
        }
        account.setBalance(account.getBalance() - convertedAmount);
        user.getCommissionStrategy().payCommission(account, convertedAmount);
        user.getTransactions().addLast(new CashWithdrawTransaction(timestamp, amount));

        return null;
    }
}
