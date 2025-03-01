package org.poo.commands;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.accounts.Account;
import org.poo.solution.Database;
import org.poo.solution.User;
import org.poo.transactions.NoClassicAccountTransaction;
import org.poo.transactions.SavingsWithdrawTransaction;
import org.poo.transactions.UserUnderAge;
import org.poo.weightedGraph.ExchangeGraph;

public final class WithdrawSavings implements Command {
    private String iban;
    private double amount;
    private String currency;
    private int timestamp;
    private Database database;

    public WithdrawSavings(final String iban, final double amount, final String currency,
                           final int timestamp, final Database database) {
        this.iban = iban;
        this.amount = amount;
        this.currency = currency;
        this.timestamp = timestamp;
        this.database = database;
    }
    @Override
    public ObjectNode execute() {
        User userToWithdraw = null;
        Account account = database.getIbanToAccount().get(iban);
        userToWithdraw = account.getOwner();

        if (userToWithdraw == null) {
            System.out.println("User not found");
            return null;
        }
        if (userToWithdraw.isAgeRestricted()) {
            userToWithdraw.getTransactions().addLast(new UserUnderAge(timestamp));
            return null;
        }
        if (!account.getType().equals("savings")) {
            System.out.println("Account is not of type savings.");
            return null;
        }
        if (!userToWithdraw.hasClassicAccount()) {
            userToWithdraw.getTransactions().addLast(new NoClassicAccountTransaction(timestamp));
            account.getTransactions().addLast(new NoClassicAccountTransaction(timestamp));
            return null;
        }

        double exchangedAmount = amount * ExchangeGraph.getInstance(null).
                getRateFromTo(currency, account.getCurrency());

        if (exchangedAmount > account.getBalance()) {
            return null;
        }

        account.setBalance(account.getBalance() - exchangedAmount);

        for (Account acc : userToWithdraw.getAccounts()) {
            if (acc.getType().equals("classic") && acc.getCurrency().equals(currency)) {
                acc.setBalance(acc.getBalance() + amount);
                userToWithdraw.getTransactions().addLast(new SavingsWithdrawTransaction(timestamp,
                        acc.getIban(), iban, amount));
                userToWithdraw.getTransactions().addLast(new SavingsWithdrawTransaction(timestamp,
                        acc.getIban(), iban, amount));
                break;
            }
        }


        return null;
    }


}
