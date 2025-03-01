package org.poo.commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.accounts.Account;
import org.poo.accounts.SavingsAccount;
import org.poo.solution.Database;
import org.poo.transactions.AddInterestTransaction;

public final class AddInterest implements Command {
    private int timestamp;
    private String iban;
    private Database database;

    public AddInterest(final int timestamp, final String iban, final Database database) {
        this.timestamp = timestamp;
        this.iban = iban;
        this.database = database;
    }

    /**
     * Executes the AddInterest command type. Increases the balance by the interest
     * percentage. Creates a json node for printing information.
     * @return An ObjectNode containing all information needed for this transaction
     */
    @Override
    public ObjectNode execute() {
        Account account = database.getIbanToAccount().get(iban);
        ObjectMapper mapper = new ObjectMapper();
        if (account.getType().equals("classic")) {
            ObjectNode mainNode = mapper.createObjectNode();
            mainNode.put("command", "addInterest");

            ObjectNode output = mapper.createObjectNode();
            output.put("timestamp", timestamp);
            output.put("description", "This is not a savings account");

            mainNode.put("output", output);
            mainNode.put("timestamp", timestamp);

            return mainNode;
        } else {
            SavingsAccount savingsAccount = (SavingsAccount) account;
            double addedAmount = savingsAccount.getBalance() * savingsAccount.getInterest();
            account.setBalance(account.getBalance() + addedAmount);
            account.getOwner().getTransactions().addLast(
                    new AddInterestTransaction(addedAmount, account.getCurrency(), timestamp));
            account.getTransactions().addLast(
                    new AddInterestTransaction(addedAmount, account.getCurrency(), timestamp));
        }
        return null;
    }
}
