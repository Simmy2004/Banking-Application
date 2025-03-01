package org.poo.commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.accounts.Account;
import org.poo.accounts.BusinessAccount;
import org.poo.solution.Database;
import org.poo.solution.User;

public final class ChangeDepositLimit implements Command {
    private int timestamp;
    private double limit;
    private String iban;
    private String mail;
    private Database database;

    public ChangeDepositLimit(final String mail, final Database database, final String iban,
                              final double limit, final int timestamp) {
        this.timestamp = timestamp;
        this.limit = limit;
        this.iban = iban;
        this.mail = mail;
        this.database = database;
    }
    @Override
    public ObjectNode execute() {
        User executer = database.getMailToUser().get(mail);
        Account account = database.getIbanToAccount().get(iban);
        if (!account.getType().equals("business")) {
            return null;
        }
        BusinessAccount businessAccount = (BusinessAccount) account;
        if (!(executer == businessAccount.getOwner())) {
            ObjectMapper mapper = new ObjectMapper();
            ObjectNode result = mapper.createObjectNode();
            result.put("command", "changeDepositLimit");

            ObjectNode output = mapper.createObjectNode();
            output.put("description", "You must be owner in order to change deposit limit.");
            output.put("timestamp", timestamp);

            result.put("output", output);
            result.put("timestamp", timestamp);
            return result;
        }

        businessAccount.setDepositLimit(limit);
        return null;
    }
}
