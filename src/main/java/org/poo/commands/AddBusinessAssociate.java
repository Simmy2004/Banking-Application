package org.poo.commands;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.accounts.Account;
import org.poo.accounts.BusinessAccount;
import org.poo.solution.Database;
import org.poo.solution.User;

public final class AddBusinessAssociate implements Command {
    private String iban;
    private String role;
    private String mail;
    private int timestamp;
    private Database database;

    public AddBusinessAssociate(final String iban, final String role, final String mail,
                                final int timestamp, final Database database) {
        this.iban = iban;
        this.role = role;
        this.mail = mail;
        this.timestamp = timestamp;
        this.database = database;
    }

    @Override
    public ObjectNode execute() {
        User added = database.getMailToUser().get(mail);
        Account account = database.getIbanToAccount().get(iban);
        if (!account.getType().equals("business")) {
            System.out.println("Account is not business");
            return null;
        }

        BusinessAccount businessAccount = (BusinessAccount) account;
        if (account.getOwner() == added
                || businessAccount.getEmployees().contains(added)
                || businessAccount.getManagers().contains(added)) {
            return null;
        }
        if (role.equals("manager")) {
            businessAccount.getManagers().addLast(added);
        } else if (role.equals("employee")) {
            businessAccount.getEmployees().addLast(added);
        }
        return null;
    }
}
