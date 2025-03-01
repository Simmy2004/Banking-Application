package org.poo.accounts;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.poo.solution.User;
import org.poo.transactions.Transaction;
import org.poo.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@NoArgsConstructor
@Data
public class Account implements SplitObserver {

    private ArrayList<Discount> availableDiscounts;
    private ArrayList<Discount> usedDiscounts;
    private String currency;
    private String iban;
    private double balance;
    private double minimumBalance;
    private ArrayList<Card> cards;
    private ArrayList<Transaction> transactions;
    private User owner;
    private double spentOnCommerciants;
    private Map<String, Integer> payToCommerciants;

    public Account(final String currency, final User owner) {
        this.currency = currency;
        this.balance = 0;
        this.iban = Utils.generateIBAN();
        cards = new ArrayList<>();
        transactions = new ArrayList<>();
        availableDiscounts = new ArrayList<>();
        usedDiscounts = new ArrayList<>();
        this.owner = owner;
        spentOnCommerciants = 0;
        payToCommerciants = new HashMap<>();
    }

    /**
     * Return the type of card classic or savings based on the class type
     * @return Type of the card as a String
     */
    public String getType() {
        return "classic";
    }

    /**
     * Function for the observer design pattern, used to notify all the split reports
     * that the user has given a response. Also adds to all the accounts of the user
     * the transaction details.
     * @param transaction The transaction that will be added to each account's list.
     * @param amount Amount that should be paid after the split.
     * @param error Null if the user doesn't have funds.
     */
    @Override
    public void update(final Transaction transaction, final double amount, final String error) {
        int index = owner.getTransactions().size();
        int timestamp = transaction.getTimestamp();
        for (int i = 0; i < owner.getTransactions().size(); i++) {
            if (owner.getTransactions().get(i).getTimestamp() > timestamp) {
                index = i;
                break;
            }
        }
        owner.getTransactions().add(index, transaction);
        if (error == null) {
            balance -= amount;
        }

    }
}
