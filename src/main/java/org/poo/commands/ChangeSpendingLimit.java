package org.poo.commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.accounts.Account;
import org.poo.accounts.BusinessAccount;
import org.poo.solution.Database;
import org.poo.solution.User;

public final class ChangeSpendingLimit implements Command {
    private String mail;
    private String iban;
    private double limit;
    private int timestamp;
    private Database database;

    public ChangeSpendingLimit(final String mail, final Database database, final String iban,
                               final double limit, final int timestamp) {
        this.mail = mail;
        this.iban = iban;
        this.limit = limit;
        this.database = database;
        this.timestamp = timestamp;
    }
    @Override
    public ObjectNode execute() {
        User executer = database.getMailToUser().get(mail);
        Account account = database.getIbanToAccount().get(iban);
        if (!account.getType().equals("business")) {
            ObjectMapper mapper = new ObjectMapper();
            ObjectNode result = mapper.createObjectNode();
            result.put("command", "changeSpendingLimit");

            ObjectNode output = mapper.createObjectNode();
            output.put("description", "This is not a business account");
            output.put("timestamp", timestamp);
            result.put("output", output);
            result.put("timestamp", timestamp);

            return result;
        }
        BusinessAccount businessAccount = (BusinessAccount) account;
        if (!(executer == businessAccount.getOwner())) {
            ObjectMapper mapper = new ObjectMapper();
            ObjectNode result = mapper.createObjectNode();
            result.put("command", "changeSpendingLimit");

            ObjectNode output = mapper.createObjectNode();
            output.put("description", "You must be owner in order to change spending limit.");
            output.put("timestamp", timestamp);

            result.put("output", output);
            result.put("timestamp", timestamp);
            return result;
        }

        businessAccount.setSpendingLimit(limit);
        return null;
    }
}
