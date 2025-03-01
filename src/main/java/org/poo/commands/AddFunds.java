package org.poo.commands;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.accounts.Account;
import org.poo.accounts.BusinessAccount;
import org.poo.accounts.BusinessDetails;
import org.poo.solution.Database;
import org.poo.solution.User;

public final class AddFunds implements Command {
    private int timestamp;
    private Database database;
    private double amount;
    private String iban;
    private String mail;

    public AddFunds(final int timestamp, final Database database,
                    final double amount, final String iban, final String mail) {
        this.timestamp = timestamp;
        this.database = database;
        this.amount = amount;
        this.iban = iban;
        this.mail = mail;
    }

    /**
     * Executes the AddFunds command. Increases balance by the given amount.
     * @return This type of command always returns a null ObjectNode, no information
     * needed to print for adding funds.
     */
    @Override
    public ObjectNode execute() {
        try {
            Account account = database.getIbanToAccount().get(iban);
            if (account == null) {
                throw new IllegalArgumentException("Invalid IBAN");
            }
            User user = database.getMailToUser().get(mail);
            if (user == null) {
                return null;
            }
            String userFullName = user.getLastName() + " " + user.getFirstName();

            if (account.getType().equals("business")) {
                BusinessAccount businessAccount = (BusinessAccount) account;
                if (businessAccount.getEmployees().contains(user)
                        && amount < businessAccount.getDepositLimit()) {
                    account.setBalance(account.getBalance() + amount);
                    businessAccount.getDetails().addLast(new BusinessDetails(timestamp,
                            userFullName, 0, amount, "NULL"));
                } else if (businessAccount.getManagers().contains(user)) {
                    account.setBalance(account.getBalance() + amount);
                    businessAccount.getDetails().addLast(new BusinessDetails(timestamp,
                            userFullName, 0, amount, "NULL"));
                } else if (businessAccount.getOwner().equals(user)) {
                    account.setBalance(account.getBalance() + amount);
                }
                return null;
            }

            account.setBalance(account.getBalance() + amount);

        } catch (IllegalArgumentException e) {
            return null;
        }
        return null;
    }
}
