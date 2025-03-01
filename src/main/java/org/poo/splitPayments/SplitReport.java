package org.poo.splitPayments;

import lombok.Data;
import org.poo.accounts.Account;
import org.poo.solution.Database;
import org.poo.transactions.CustomSplitTransaction;
import org.poo.transactions.EqualSplitTransaction;
import org.poo.transactions.OneRejectedTransaction;
import org.poo.transactions.Transaction;
import org.poo.weightedGraph.ExchangeGraph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public final class SplitReport {
    private Map<String, Boolean> acceptMap;
    private int timestamp;
    private List<String> ibans;
    private List<Double> amounts;
    private String type;
    private String currency;
    private double totalAmount;
    private Database database;
    private int stopCount;
    private List<String> constIbans;

    public SplitReport(final int timestamp, final List<String> ibans, final List<Double> amounts,
                       final String type, final String currency, final double totalAmount,
                       final Database database) {
        this.timestamp = timestamp;
        this.ibans = ibans;
        this.amounts = amounts;
        this.acceptMap = new HashMap<>();
        this.type = type;
        this.currency = currency;
        this.totalAmount = totalAmount;
        this.database = database;
        stopCount = ibans.size();
        constIbans = new ArrayList<>();
        constIbans.addAll(ibans);
    }

    public void userActioned(final String acceptedIban, final boolean decision) {
        acceptMap.put(acceptedIban, decision);
        String errorAccount = getAccountNoBalance();

        ArrayList<Account> accounts = new ArrayList<>();
        for (String iban : constIbans) {
            accounts.add(database.getIbanToAccount().get(iban));
        }
        Transaction transaction = null;

        ibans.remove(acceptedIban);


        if (acceptMap.size() == stopCount && acceptMap.containsValue(false)) {
            //someone rejected.
            transaction = new OneRejectedTransaction(type, amounts, currency,
                    constIbans, timestamp);
        } else if (acceptMap.size() == stopCount && type.equals("equal")) {
            //No one rejected equal split.
            transaction = new EqualSplitTransaction(timestamp, currency,
                    totalAmount, constIbans, errorAccount);
        } else if (acceptMap.size() == stopCount && type.equals("custom")) {
            transaction = new CustomSplitTransaction(timestamp, currency,
                    totalAmount, amounts, constIbans, errorAccount);
        }

        if (transaction != null) {
            int i = 0;
            for (Account account : accounts) {
                double exchangedAmount =  amounts.get(i) * ExchangeGraph.getInstance(null)
                        .getRateFromTo(currency, account.getCurrency());

                account.update(transaction, exchangedAmount, errorAccount);
                i++;
            }
            database.getSplits().remove(this);
        }

    }

    /**
     * Searches for an account in the split that has no funds.
     * @return Returns the iban of one account that has no funds for the split payment.
     */
    private String getAccountNoBalance() {
        int i = 0;
        Account noBalancePerson = null;
        for (String iban : constIbans) {
            Account participant = database.getIbanToAccount().get(iban);
            double exchangeRate = ExchangeGraph.getInstance(null).
                    getRateFromTo(currency, participant.getCurrency());

            double amountAfterExchange = amounts.get(i) * exchangeRate;
            if (amountAfterExchange > participant.getBalance()) {
                noBalancePerson = participant;
                break;
            }
            i++;
        }

        if (noBalancePerson != null) {
            return noBalancePerson.getIban();
        }
        return null;
    }

}
