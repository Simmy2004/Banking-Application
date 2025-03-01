package org.poo.commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.accounts.Account;
import org.poo.accounts.BusinessAccount;
import org.poo.accounts.BusinessDetails;
import org.poo.accounts.Card;
import org.poo.commissionStrategies.GoldStudentStrategy;
import org.poo.solution.Commerciant;
import org.poo.solution.Database;
import org.poo.solution.ServicePlan;
import org.poo.solution.User;
import org.poo.transactions.CardIsFrozen;
import org.poo.transactions.CardPayment;
import org.poo.transactions.InsufficientFunds;
import org.poo.transactions.PlanUpgradedTransaction;
import org.poo.weightedGraph.ExchangeGraph;

import java.awt.*;

public class PayOnline implements Command {
    private Database database;
    private String cardNumber;
    private double amount;
    private String currency;
    private int timestamp;
    private String description;
    private String commerciantName;
    private String email;

    public PayOnline(final String cardNumber, final double amount, final String currency,
                     final int timestamp, final String description, final String commerciant,
                     final String email, final Database database) {
        this.cardNumber = cardNumber;
        this.amount = amount;
        this.currency = currency;
        this.timestamp = timestamp;
        this.description = description;
        this.commerciantName = commerciant;
        this.email = email;
        this.database = database;
    }

    /**
     * Executes the payOnline command. Subtracts the price from the account balance
     * if the payment is successful. Returns error ObjectNodes if funds are insufficient
     * or if the card is not found. Payment is made between different currencies.
     * Currencies are translated with the help of a graph.
     * @return An output ObjectNode if the card is not found, containing information
     * about this specific error.
     */
    @Override
    public ObjectNode execute() {
        try {

            Account cardAccount = database.getCardNumberToAccount().get(cardNumber);
            User user = database.getMailToUser().get(email);
            Commerciant commerciant = database.getCommerciantByName(commerciantName);

            ObjectMapper mapper = new ObjectMapper();
            if (amount == 0) {
                return null;
            }

            if (cardAccount == null || commerciant == null
                    || (cardAccount.getType().equals("business")
                    && userRestricted(user, cardAccount))
                    || (!cardAccount.getType().equals("business")
                    && cardAccount.getOwner() != user)) {
                ObjectNode mainNode = mapper.createObjectNode();
                mainNode.put("command", "payOnline");

                ObjectNode output = mapper.createObjectNode();
                output.put("timestamp", timestamp);
                output.put("description", "Card not found");

                mainNode.set("output", output);
                mainNode.put("timestamp", timestamp);
                return mainNode;
            }


            double amountInRon =  ExchangeGraph.getInstance(null).
                    getRateFromTo(currency, "RON") * amount;

            double exchangeRate = ExchangeGraph.getInstance(null).
                    getRateFromTo(currency, cardAccount.getCurrency());
            amount = amount * exchangeRate;

            /* If card is frozen */
            for (Card card : cardAccount.getCards()) {
                if (card.getNumber().equals(cardNumber) && card.getStatus().equals("frozen")) {
                    user.getTransactions().addLast(new CardIsFrozen(timestamp));
                    return null;
                }
            }

            /* If card doesn't have funds */
            if (cardAccount.getBalance() < amount) {
                user.getTransactions().addLast(new InsufficientFunds(timestamp));
                return null;
            }

            if (cardAccount.getType().equals("business")) {
                String fullName = user.getLastName() + " " + user.getFirstName();
                BusinessAccount businessAccount = (BusinessAccount) cardAccount;
                if (businessAccount.getEmployees().contains(user)
                        && amount > businessAccount.getSpendingLimit()) {
                    return null;
                }

                if (businessAccount.getEmployees().contains(user)) {
                    businessAccount.getDetails().addLast(new BusinessDetails(timestamp,
                            fullName, amount, 0, commerciantName));
                } else if (businessAccount.getManagers().contains(user)) {
                    businessAccount.getDetails().addLast(new BusinessDetails(timestamp,
                            fullName, amount, 0, commerciantName));
                }
            }


            cardAccount.setBalance(cardAccount.getBalance() - amount);
            user.getTransactions().addLast(new CardPayment(timestamp, amount, commerciantName));
            cardAccount.getTransactions().
                    addLast(new CardPayment(timestamp, amount, commerciantName));

            if (commerciant.getStrategy().equals("nrOfTransactions")) {
                int oldValue = cardAccount.getPayToCommerciants().
                        getOrDefault(commerciantName, 0);
                cardAccount.getPayToCommerciants().put(commerciantName, oldValue + 1);
            }

            if (amountInRon > 300) {
                user.setTransactionsOverLimit(user.getTransactionsOverLimit() + 1);
            }
            if (user.getTransactionsOverLimit() == 5 && user.getServicePlan().
                    equals(ServicePlan.SILVER)) {
                user.setCommissionStrategy(new GoldStudentStrategy());
                user.getTransactions().addLast(new PlanUpgradedTransaction(timestamp, "gold",
                        cardAccount.getIban()));
                user.setServicePlan(ServicePlan.GOLD);
                user.setTransactionsOverLimit(99);
            }

            cardAccount.getOwner().getCommissionStrategy().payCommission(cardAccount, amount);
            commerciant.getCashbackStrategy().getCashBack(cardAccount, commerciant, amount);

            for (Card card : cardAccount.getCards()) {
                if (card.getNumber().equals(cardNumber)) {
                    card.pay(database, timestamp);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private boolean userRestricted(final User user, final Account cardAccount) {
        BusinessAccount businessAccount = null;
        if (cardAccount.getType().equals("business")) {
            businessAccount = (BusinessAccount) cardAccount;
        }

        if (businessAccount == null) {
            return false;
        }

        if (!businessAccount.getEmployees().contains(user)
                && !businessAccount.getManagers().contains(user)
                && !businessAccount.getOwner().equals(user)) {
            return true;
        }
        return false;
    }
}
