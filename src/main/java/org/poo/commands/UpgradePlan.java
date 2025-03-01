package org.poo.commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.accounts.Account;
import org.poo.commissionStrategies.GoldStudentStrategy;
import org.poo.commissionStrategies.SilverStrategy;
import org.poo.solution.Database;
import org.poo.solution.ServicePlan;
import org.poo.solution.User;
import org.poo.transactions.InsufficientFunds;
import org.poo.transactions.PlanUpgradedTransaction;
import org.poo.transactions.SamePlanTransaction;
import org.poo.weightedGraph.ExchangeGraph;

public final class UpgradePlan implements Command {
    private String planString;
    private String iban;
    private int timestamp;
    private Database database;

    public UpgradePlan(final String planString, final String iban,
                       final int timestamp, final Database database) {
        this.planString = planString;
        this.iban = iban;
        this.timestamp = timestamp;
        this.database = database;
    }

    @Override
    public ObjectNode execute() {
        ServicePlan wantedPlan;
        Account account = database.getIbanToAccount().get(iban);
        if (account == null) {
            ObjectMapper mapper = new ObjectMapper();
            ObjectNode result = mapper.createObjectNode();
            result.put("command", "upgradePlan");

            ObjectNode output = mapper.createObjectNode();
            output.put("description", "Account not found");
            output.put("timestamp", timestamp);
            result.put("output", output);
            result.put("timestamp", timestamp);

            return result;
        }
        User owner = account.getOwner();

        switch (planString) {
            case "silver" -> wantedPlan = ServicePlan.SILVER;
            case "gold" -> wantedPlan = ServicePlan.GOLD;
            case "standard" -> wantedPlan = ServicePlan.STANDARD;
            default -> wantedPlan = ServicePlan.STUDENT;
        }

        if (owner.getServicePlan() == wantedPlan) {
            account.getTransactions().addLast(new SamePlanTransaction(timestamp, planString));
            owner.getTransactions().addLast(new SamePlanTransaction(timestamp, planString));
            return null;
        }
        if (wantedPlan.ordinal() < owner.getServicePlan().ordinal()) {
            return null;
        }

        double price = getUpgradePrice(wantedPlan, owner.getServicePlan());
        price *= ExchangeGraph.getInstance(null).getRateFromTo("RON", account.getCurrency());

        if (price > account.getBalance()) {
            owner.getTransactions().addLast(new InsufficientFunds(timestamp));
            return null;
        }

        account.setBalance(account.getBalance() - price);
        owner.setServicePlan(wantedPlan);
        if (wantedPlan == ServicePlan.SILVER) {
            owner.setCommissionStrategy(new SilverStrategy());
        } else {
            owner.setCommissionStrategy(new GoldStudentStrategy());
        }
        owner.getTransactions().addLast(new PlanUpgradedTransaction(timestamp, planString, iban));
        account.getTransactions().
                addLast(new PlanUpgradedTransaction(timestamp, planString, iban));

        return null;
    }

    private double getUpgradePrice(final ServicePlan wantedPlan, final ServicePlan currentPLan) {
        if (wantedPlan == ServicePlan.SILVER) {
            return 100.0;
        }
        if (wantedPlan == ServicePlan.GOLD && currentPLan == ServicePlan.SILVER) {
            return 250.0;
        }
        if (wantedPlan == ServicePlan.GOLD && currentPLan == ServicePlan.STUDENT
                || wantedPlan == ServicePlan.GOLD && currentPLan == ServicePlan.STANDARD) {
            return 350.0;
        }
        return 0;
    }
}
