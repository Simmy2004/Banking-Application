package org.poo.commands;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.accounts.Account;
import org.poo.accounts.Card;
import org.poo.accounts.OneTimeCard;
import org.poo.solution.Database;
import org.poo.solution.User;
import org.poo.transactions.CreateCardTransaction;

public class CreateOneTimeCard implements Command {
    private int timestamp;
    private Database database;
    private String iban;
    private String email;


    public CreateOneTimeCard(final int timestamp, final Database database,
                             final String iban, final String email) {
        this.timestamp = timestamp;
        this.database = database;
        this.iban = iban;
        this.email = email;
    }

    /**
     * Executes the createOneTimeCard command. Adds e new OneTimeCard to the given account.
     * This card will change it's number after every payment made. Updates the database
     * for future reaches to this card and adds the transaction in the user's history.
     * @return This command always return a null ObjectNode because no information about
     * this command is requested.
     */
    @Override
    public ObjectNode execute() {
        try {
            Account account = database.getIbanToAccount().get(iban);
            if (account == null) {
                throw new IllegalArgumentException("Invalid IBAN");
            }
            Card addedCard = new OneTimeCard(email);
            account.getCards().add(addedCard);
            database.getCardNumberToAccount().put(addedCard.getNumber(), account);

            User user = database.getMailToUser().get(email);
            if (user == null) {
                throw new IllegalArgumentException("Invalid email");
            }
            user.getTransactions().addLast(new CreateCardTransaction(timestamp,
                    addedCard.getNumber(), email, iban));

            account.getTransactions().addLast(new CreateCardTransaction(timestamp,
                    addedCard.getNumber(), email, iban));

        } catch (IllegalArgumentException e) {
            return null;
        }
        return null;
    }
}
