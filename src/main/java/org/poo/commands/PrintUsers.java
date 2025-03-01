package org.poo.commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.accounts.Account;
import org.poo.accounts.Card;
import org.poo.solution.Database;
import org.poo.solution.User;

public class PrintUsers implements Command {
    private int timestamp;
    private Database database;

    public PrintUsers(final int timestamp, final Database database) {
        this.timestamp = timestamp;
        this.database = database;
    }

    /**
     * Executes the printUsersCommand. Prints all the users containing their accounts and cards
     * found in the database.
     * @return An ObjectNode containing information about al the users found in the database.
     */
    public ObjectNode execute() {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode mainObject = mapper.createObjectNode();
        mainObject.put("command", "printUsers");

        ArrayNode usersArray = mapper.createArrayNode();
        for (User user : database.getUsers()) {
            ObjectNode userObject = mapper.createObjectNode();
            userObject.put("firstName", user.getFirstName());
            userObject.put("lastName", user.getLastName());
            userObject.put("email", user.getEmail());

            ArrayNode accountsArray = mapper.createArrayNode();
            /* If user doesn't have any accounts */
            if (user.getAccounts() == null) {
                return null;
            }

            for (Account account : user.getAccounts()) {
                ObjectNode accountObject = mapper.createObjectNode();
                accountObject.put("IBAN", account.getIban());
                accountObject.put("balance", account.getBalance());
                accountObject.put("currency", account.getCurrency());
                accountObject.put("type", account.getType());

                ArrayNode cardsArray = mapper.createArrayNode();

                /* If no cards in the account */
                if (account.getCards() == null) {
                    return null;
                }

                for (Card card : account.getCards()) {
                    ObjectNode cardObject = mapper.createObjectNode();
                    cardObject.put("cardNumber", card.getNumber());
                    cardObject.put("status", card.getStatus());
                    cardsArray.add(cardObject);
                }
                accountObject.put("cards", cardsArray);
                accountsArray.add(accountObject);
            }
            userObject.put("accounts", accountsArray);
            usersArray.add(userObject);
        }
        mainObject.put("output", usersArray);
        mainObject.put("timestamp", timestamp);

        return mainObject;
    }
}
