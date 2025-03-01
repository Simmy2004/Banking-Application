package org.poo.commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.accounts.Account;
import org.poo.solution.Commerciant;
import org.poo.solution.Database;
import org.poo.solution.User;
import org.poo.transactions.InsufficientFunds;
import org.poo.transactions.ReceiveMoneyTransaction;
import org.poo.transactions.SendMoneyTransaction;
import org.poo.weightedGraph.ExchangeGraph;

public class SendMoney implements Command {
    private String senderIban;
    private String receiverIban;
    private double amount;
    private int timestamp;
    private String senderEmail;
    private String description;
    private Database database;

    public SendMoney(final String senderIban, final String receiverIban, final double amount,
                     final String senderEmail, final String description, final Database database,
                     final int timestamp) {
        /* The sender and receiver Iban may be sent as an alias. */
        this.senderIban = senderIban;
        this.receiverIban = receiverIban;
        this.amount = amount;
        this.senderEmail = senderEmail;
        this.description = description;
        this.database = database;
        this.timestamp = timestamp;
    }

    /**
     * Executes the sendMoney command. Searches receiver and sender by the iban or by alias.
     * Subtracts money from the sender and gives it to the receiver account. Transfers are
     * between different currencies, the translation is made with a graph.
     * @return This command always return a null ObjectNode on execution, because no information
     * is requested for this transaction.
     */
    @Override
    public ObjectNode execute() {
        try {
            Account sender = database.getIbanToAccount().get(senderIban);
            Account receiver = database.getIbanToAccount().get(receiverIban);

            if (sender == null) {
                sender = database.getAliasToAccount().get(senderIban);
                if (sender == null) {
                    ObjectMapper mapper = new ObjectMapper();
                    ObjectNode result = mapper.createObjectNode();
                    result.put("command", "sendMoney");

                    ObjectNode output = mapper.createObjectNode();
                    output.put("description", "User not found");
                    output.put("timestamp", timestamp);

                    result.put("output", output);
                    result.put("timestamp", timestamp);
                    return result;
                }
            }

            User user = database.getMailToUser().get(senderEmail);
            if (sender.getBalance() - user.getCommissionStrategy().
                    getCommission(sender, amount) < amount) {
                user.getTransactions().addLast(new InsufficientFunds(timestamp));

                sender.getTransactions().addLast(new InsufficientFunds(timestamp));
                return null;
            }

            if (receiver == null) {
                receiver = database.getAliasToAccount().get(receiverIban);
                if (receiver == null) {
                    return makeCommerciantPayment();
                }

            }

            double exchangeRate = ExchangeGraph.getInstance(null).
                    getRateFromTo(sender.getCurrency(), receiver.getCurrency());

            sender.setBalance(sender.getBalance() - amount);
            receiver.setBalance(receiver.getBalance() + amount * exchangeRate);

            user.getCommissionStrategy().payCommission(sender, amount);

            User receiveUser = null;
            for (User u : database.getUsers()) {
                if (u.getAccounts().contains(receiver)) {
                    receiveUser = u;
                    break;
                }
            }

            user.getTransactions().addLast(new SendMoneyTransaction(timestamp, description,
                    senderIban, receiverIban, amount, sender.getCurrency()));
            sender.getTransactions().addLast(new SendMoneyTransaction(timestamp, description,
                    senderIban, receiverIban, amount, sender.getCurrency()));

            receiveUser.getTransactions().addLast(new ReceiveMoneyTransaction(timestamp,
                    description, senderIban, receiverIban, amount * exchangeRate,
                    receiver.getCurrency()));

            receiver.getTransactions().addLast(new ReceiveMoneyTransaction(timestamp, description,
                    senderIban, receiverIban, amount * exchangeRate, receiver.getCurrency()));

        } catch (Exception e) {
        }

        return null;
    }

    private ObjectNode makeCommerciantPayment() {
        User user = database.getMailToUser().get(senderEmail);
        Account sender = database.getIbanToAccount().get(senderIban);
        Commerciant receiver = null;
        for (Commerciant commerciant : database.getCommerciants()) {
            if (commerciant.getAccount().equals(receiverIban)) {
                receiver = commerciant;
                break;
            }
        }
        if (receiver == null) {
            ObjectMapper mapper = new ObjectMapper();
            ObjectNode result = mapper.createObjectNode();
            result.put("command", "sendMoney");

            ObjectNode output = mapper.createObjectNode();
            output.put("description", "User not found");
            output.put("timestamp", timestamp);

            result.put("output", output);
            result.put("timestamp", timestamp);
            return result;
        }

        sender.setBalance(sender.getBalance() - amount);
        user.getCommissionStrategy().payCommission(sender, amount);
        receiver.getCashbackStrategy().getCashBack(sender, receiver, amount);
        if (receiver.getStrategy().equals("nrOfTransactions")) {
            int oldValue = sender.getPayToCommerciants().getOrDefault(receiver.getName(), 0);
            sender.getPayToCommerciants().put(receiver.getName(), oldValue + 1);
        }

        user.getTransactions().addLast(new SendMoneyTransaction(timestamp, description,
                senderIban, receiverIban, amount, sender.getCurrency()));
        return null;
    }
}
