package org.poo.commands;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.accounts.Account;
import org.poo.accounts.BusinessAccount;
import org.poo.accounts.SavingsAccount;
import org.poo.solution.Database;
import org.poo.solution.User;
import org.poo.transactions.CreateAccountTransaction;

public final class AddAccount implements Command {
    private final int timestamp;
    private final Database database;

    private final String email;
    private final String currency;
    private final String type;
    private final double interest;

    public AddAccount(final int timestamp, final Database database, final String email,
                      final String currency, final String type, final double interest) {
        this.timestamp = timestamp;
        this.database = database;
        this.email = email;
        this.currency = currency;
        this.type = type;
        this.interest = interest;
    }

    /**
     * Creates a ObjectNode type containing all the information needed about this type
     * of command. Adds a new account to the executing user and updates the database.
     * @return This type of command will always return a null ObjectNode because
     * no information is needed from adding an account.
     */
    public ObjectNode execute() {
        try {
            User userCreatingAccount = database.getMailToUser().get(email);
            if (userCreatingAccount == null) {
                throw new IllegalArgumentException("User not found");
            }
            Account addedAccount;
            if (type.equals("classic")) {
                addedAccount = new Account(currency, userCreatingAccount);
            } else if (type.equals("savings")) {
                addedAccount = new SavingsAccount(currency, userCreatingAccount, interest);
            } else {
                addedAccount = new BusinessAccount(currency, userCreatingAccount);
            }

            userCreatingAccount.getAccounts().add(addedAccount);
            database.getIbanToAccount().put(addedAccount.getIban(), addedAccount);

            userCreatingAccount.getTransactions().addLast(new CreateAccountTransaction(timestamp));
            addedAccount.getTransactions().addLast(new CreateAccountTransaction(timestamp));

        } catch (IllegalArgumentException e) {
            return null;
        }

        return null;
    }
}
