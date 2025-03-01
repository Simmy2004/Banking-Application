package org.poo.commands;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.accounts.Account;
import org.poo.accounts.Card;
import org.poo.solution.Database;
import org.poo.solution.User;
import org.poo.transactions.CreateCardTransaction;

public class CreateCard implements Command {
    private int timestamp;
    private Database database;
    private String iban;
    private String email;


    public CreateCard(final int timestamp, final Database database,
                      final String iban, final String email) {
        this.timestamp = timestamp;
        this.database = database;
        this.iban = iban;
        this.email = email;
    }

    /**
     * Executes the CreateCard command. Creates a new card, adds it to the given account.
     * Updates the database for the given user
     * @return This command always return a null ObjectNode because no information about
     * this transaction is requested further.
     */
    @Override
    public ObjectNode execute() {
        try {
            Account account = database.getIbanToAccount().get(iban);
            if (account == null) {
                throw new IllegalArgumentException("Invalid IBAN");
            }
            User user = database.getMailToUser().get(email);
            if (user == null || !user.getAccounts().contains(account)) {
                throw new IllegalArgumentException("Invalid email" + timestamp);
            }

            Card addedCard = new Card(email);
            account.getCards().add(addedCard);
            database.getCardNumberToAccount().put(addedCard.getNumber(), account);


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
