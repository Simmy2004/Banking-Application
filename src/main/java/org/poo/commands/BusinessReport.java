package org.poo.commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.accounts.Account;
import org.poo.accounts.BusinessAccount;
import org.poo.accounts.BusinessDetails;
import org.poo.solution.Commerciant;
import org.poo.solution.Database;
import org.poo.solution.User;

import java.util.ArrayList;
import java.util.Objects;

public final class BusinessReport implements Command {
    private int start;
    private int end;
    private int timestamp;
    private String iban;
    private String type;
    private Database database;

    public BusinessReport(final int start, final int end, final int timestamp, final String iban,
                          final String type, final Database database) {
        this.start = start;
        this.end = end;
        this.timestamp = timestamp;
        this.iban = iban;
        this.type = type;
        this.database = database;
    }
    @Override
    public ObjectNode execute() {
        if (type.equals("commerciant")) {
            return executeCommerciantReport();
        }
        double allSpends = 0;
        double allDeposits = 0;
        ObjectMapper mapper = new ObjectMapper();
        Account account = database.getIbanToAccount().get(iban);
        if (account == null) {
            System.out.println("Account not found");
            return null;
        }
        if (!Objects.equals(account.getType(), "business")) {
            System.out.println("Account not of type business");
            return null;
        }
        BusinessAccount businessAccount = (BusinessAccount) account;

        ObjectNode mainNode = mapper.createObjectNode();
        mainNode.put("command", "businessReport");

        ObjectNode output = mapper.createObjectNode();
        output.put("IBAN", iban);
        output.put("balance", businessAccount.getBalance());
        output.put("currency", businessAccount.getCurrency());
        output.put("spending limit", businessAccount.getSpendingLimit());
        output.put("deposit limit", businessAccount.getDepositLimit());
        output.put("statistics type", type);

        ArrayNode managerNode = mapper.createArrayNode();
        ArrayNode employeeNode = mapper.createArrayNode();
        for (User manager : businessAccount.getManagers()) {
            double totalSpend = 0;
            double totalDeposit = 0;
            String fullName = manager.getLastName() + " " + manager.getFirstName();
            for (BusinessDetails detail : businessAccount.getDetails()) {
                if (detail.getName().equals(fullName)
                        && detail.getTimestamp() <= end && detail.getTimestamp() >= start) {
                    totalSpend += detail.getSpent();
                    totalDeposit += detail.getDeposited();
                }
            }

            ObjectNode detailNode = mapper.createObjectNode();
            detailNode.put("username", fullName);
            detailNode.put("spent", totalSpend);
            detailNode.put("deposited", totalDeposit);
            managerNode.add(detailNode);

            allSpends += totalSpend;
            allDeposits += totalDeposit;
        }

        for (User employee : businessAccount.getEmployees()) {
            double totalSpend = 0;
            double totalDeposit = 0;
            String fullName = employee.getLastName() + " " + employee.getFirstName();
            for (BusinessDetails detail : businessAccount.getDetails()) {
                if (detail.getName().equals(fullName)
                        && detail.getTimestamp() <= end && detail.getTimestamp() >= start) {

                    totalSpend += detail.getSpent();
                    totalDeposit += detail.getDeposited();
                }
            }

            ObjectNode detailNode = mapper.createObjectNode();
            detailNode.put("username", fullName);
            detailNode.put("spent", totalSpend);
            detailNode.put("deposited", totalDeposit);
            employeeNode.add(detailNode);

            allSpends += totalSpend;
            allDeposits += totalDeposit;
        }
        output.put("managers", managerNode);
        output.put("employees", employeeNode);
        output.put("total spent", allSpends);
        output.put("total deposited", allDeposits);
        mainNode.put("output", output);
        mainNode.put("timestamp", timestamp);


        return mainNode;
    }

    private ObjectNode executeCommerciantReport() {
        ObjectMapper mapper = new ObjectMapper();
        Account account = database.getIbanToAccount().get(iban);
        if (account == null) {
            System.out.println("Account not found");
            return null;
        }
        if (!Objects.equals(account.getType(), "business")) {
            System.out.println("Account not of type business");
            return null;
        }
        BusinessAccount businessAccount = (BusinessAccount) account;

        ObjectNode mainNode = mapper.createObjectNode();
        mainNode.put("command", "businessReport");
        ObjectNode output = mapper.createObjectNode();
        output.put("balance", businessAccount.getBalance());

        ArrayNode commerciantNode = mapper.createArrayNode();

        ArrayList<String> employeeNames = new ArrayList<>();
        ArrayList<String> managerNames = new ArrayList<>();
        for (User manager : businessAccount.getManagers()) {
            managerNames.add(manager.getLastName() + " " + manager.getFirstName());
        }

        for (User employee : businessAccount.getEmployees()) {
            employeeNames.add(employee.getLastName() + " " + employee.getFirstName());
        }

        for (Commerciant commerciant : database.getCommerciants()) {
            ArrayNode employeeNode = mapper.createArrayNode();
            ArrayNode managerNode = mapper.createArrayNode();

            double totalReceived = 0;
            String fullName = commerciant.getName();
            for (BusinessDetails detail : businessAccount.getDetails()) {
                if (detail.getCommerciant().equals(fullName)
                        && detail.getTimestamp() <= end && detail.getTimestamp() >= start) {
                    totalReceived += detail.getSpent();

                    if (employeeNames.contains(detail.getName())) {
                        employeeNode.add(detail.getName());
                    }
                    if (managerNames.contains(detail.getName())) {
                        managerNode.add(detail.getName());
                    }
                }
            }
            if (totalReceived > 0) {

                ObjectNode detailNode = mapper.createObjectNode();
                detailNode.put("commerciant", fullName);
                detailNode.put("employees", employeeNode);
                detailNode.put("managers", managerNode);
                detailNode.put("total received", totalReceived);

                commerciantNode.add(detailNode);
            }

        }
        output.put("commerciants", commerciantNode);
        output.put("currency", businessAccount.getCurrency());
        output.put("deposit limit", businessAccount.getDepositLimit());
        output.put("IBAN", businessAccount.getIban());
        output.put("spending limit", businessAccount.getSpendingLimit());
        output.put("statistics type", type);

        mainNode.put("output", output);
        mainNode.put("timestamp", timestamp);
        return mainNode;
    }
}
