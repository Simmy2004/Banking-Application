package org.poo.commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.accounts.Account;
import org.poo.accounts.SavingsAccount;
import org.poo.solution.Database;
import org.poo.solution.User;
import org.poo.transactions.ChangeRateTransaction;

public final class ChangeInterest implements Command {
    private int timestamp;
    private String iban;
    private double rate;
    private Database database;

    public ChangeInterest(final int timestamp, final String iban,
                          final double rate, final Database database) {
        this.timestamp = timestamp;
        this.iban = iban;
        this.rate = rate;
        this.database = database;
    }

    /**
     * Executes the ChangeInterest command type. Sets a new interest rate for
     * the savings account. Throws an error if the account is not made for savings.
     * Updates the database by adding this transaction in user's history.
     * @return An ObjectNode containing all information needed about this transaction.
     */
    @Override
    public ObjectNode execute() {
        Account account = database.getIbanToAccount().get(iban);
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode mainNode = mapper.createObjectNode();
        if (account.getType().equals("classic")) {
            mainNode.put("command", "changeInterestRate");

            ObjectNode output = mapper.createObjectNode();
            output.put("timestamp", timestamp);
            output.put("description", "This is not a savings account");

            mainNode.put("output", output);
            mainNode.put("timestamp", timestamp);
            return mainNode;
        } else {
            SavingsAccount savingsAccount = (SavingsAccount) account;
            savingsAccount.setInterest(rate);
        }

        Account owner = database.getIbanToAccount().put(iban, account);

        for (User user : database.getUsers()) {
            if (user.getAccounts().contains(owner)) {
                user.getTransactions().addLast(new ChangeRateTransaction(timestamp, rate));
                owner.getTransactions().addLast(new ChangeRateTransaction(timestamp, rate));
                return null;
            }
        }
        return null;
    }
}
