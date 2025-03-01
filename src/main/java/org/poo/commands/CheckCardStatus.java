package org.poo.commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.accounts.Account;
import org.poo.accounts.Card;
import org.poo.solution.Database;
import org.poo.solution.User;
import org.poo.transactions.WillBeFrozen;

public class CheckCardStatus implements Command {
    private int timestamp;
    private String cardNumber;
    private Database database;

    public CheckCardStatus(final int timestamp, final String cardNumber,
                           final Database database) {
        this.timestamp = timestamp;
        this.cardNumber = cardNumber;
        this.database = database;
    }

    /**
     * Executes the CheckCardStatus command type. Updates the card status on use
     * if the balance comes below the minimum threshold set. Adds this transaction
     * in the database user history.
     * @return An ObjectNode containing information about the errors occurred such as
     * not fining the card, or the user.
     */
    @Override
    public ObjectNode execute() {
        Account owner = database.getCardNumberToAccount().get(cardNumber);
        if (owner == null) {
            ObjectMapper mapper = new ObjectMapper();
            ObjectNode result = mapper.createObjectNode();
            result.put("command", "checkCardStatus");

            ObjectNode output = mapper.createObjectNode();
            output.put("timestamp", timestamp);
            output.put("description", "Card not found");

            result.put("output", output);
            result.put("timestamp", timestamp);
            return result;
        }

        if (owner.getBalance() <= owner.getMinimumBalance()) {
            boolean cardFound = false;
            for (Card card : owner.getCards()) {
                if (card.getNumber().equals(cardNumber)) {
                    card.setStatus("frozen");
                    cardFound = true;
                }
            }
            if (cardFound) {
                for (User user : database.getUsers()) {
                    if (user.getAccounts().contains(owner)) {
                        user.getTransactions().addLast(new WillBeFrozen(timestamp));
                    }
                }
            }
        }
        return null;
    }
}
