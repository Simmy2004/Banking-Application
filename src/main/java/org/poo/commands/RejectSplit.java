package org.poo.commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.accounts.Account;
import org.poo.solution.Database;
import org.poo.solution.User;
import org.poo.splitPayments.SplitReport;

import java.util.Objects;

public final class RejectSplit implements Command {
    private int timestamp;
    private Database database;
    private String mail;
    private String type;

    public RejectSplit(final String mail, final String type,
                       final int timestamp, final Database db) {
        this.timestamp = timestamp;
        this.mail = mail;
        this.type = type;
        this.database = db;
    }

    @Override
    public ObjectNode execute() {
        User owner = database.getMailToUser().get(mail);
        if (owner == null) {
            ObjectMapper mapper = new ObjectMapper();
            ObjectNode result = mapper.createObjectNode();
            result.put("command", "rejectSplitPayment");

            ObjectNode output = mapper.createObjectNode();
            output.put("description", "User not found");
            output.put("timestamp", timestamp);
            result.put("output", output);
            result.put("timestamp", timestamp);

            return result;
        }
        SplitReport report = getWantedReport();
        if (report == null) {
            return null;
        }

        Account searchedAccount = null;
        for (Account account : owner.getAccounts()) {
            if (report.getIbans().contains(account.getIban())) {
                searchedAccount = account;
                break;
            }
        }
        if (searchedAccount == null) {
            return null;
        }

        report.userActioned(searchedAccount.getIban(), false);

        return null;
    }

    private SplitReport getWantedReport() {
        User owner = database.getMailToUser().get(mail);
        if (owner == null) {
            return null;
        }
        for (SplitReport report : database.getSplits()) {
            for (Account account : owner.getAccounts()) {
                if (report.getIbans().contains(account.getIban())
                        && Objects.equals(type, report.getType())) {
                    return report;
                }
            }
        }
        return null;
    }
}
