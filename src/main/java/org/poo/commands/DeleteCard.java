package org.poo.commands;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.accounts.Account;
import org.poo.accounts.BusinessAccount;
import org.poo.accounts.Card;
import org.poo.solution.Database;
import org.poo.solution.User;
import org.poo.transactions.DestroyedCard;

import java.util.Objects;

public class DeleteCard implements Command {
    private int timestamp;
    private Database database;
    private String email;
    private String cardNumber;

    public DeleteCard(final int timestamp, final Database database,
                      final String cardNumber, final String email) {
        this.timestamp = timestamp;
        this.database = database;
        this.email = email;
        this.cardNumber = cardNumber;
    }

    /**
     * Executes the deleteCard command. Deletes the card from the account list of cards,
     * removes it from the database and adds this process to the user's history.
     * @return This command will always return a null ObjectNode, because no information
     * is requested about this process.
     */
    @Override
    public ObjectNode execute() {
        try {
            Account account = database.getCardNumberToAccount().get(cardNumber);
            User executer = database.getMailToUser().get(email);

            if (account == null || executer == null) {
                throw new IllegalArgumentException("Card not found");
            }
            if (account.getBalance() != 0 || account.getOwner() != executer) {
                return null;
            }
            Card deletedCard = null;
            for (Card card : account.getCards()) {
                if (card.getNumber().equals(cardNumber)) {
                    deletedCard = card;
                    break;
                }
            }

            if (account.getType().equals("business") && deletedCard != null) {
                BusinessAccount businessAccount = (BusinessAccount) account;
                if (businessAccount.getEmployees().contains(executer)
                        && !Objects.equals(deletedCard.getOwnerMail(), email)) {
                    System.out.println("Employee can only delete his own card");
                    return null;
                }
            }

            for (Card card : account.getCards()) {
                if (card.getNumber().equals(cardNumber)) {
                    account.getCards().remove(card);
                    database.getCardNumberToAccount().remove(cardNumber);

                    User user = database.getMailToUser().get(email);
                    user.getTransactions().addLast(new DestroyedCard(timestamp,
                            cardNumber, email, account.getIban()));
                    break;
                }
            }
        } catch (Exception e) {
        }

        return null;
    }
}
