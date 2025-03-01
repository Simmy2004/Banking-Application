package org.poo.accounts;

import org.poo.solution.Database;
import org.poo.solution.User;
import org.poo.transactions.CreateCardTransaction;
import org.poo.transactions.DestroyedCard;
import org.poo.utils.Utils;

public final class OneTimeCard extends Card {

    public OneTimeCard(final String ownerMail) {
        super(ownerMail);
    }

    @Override
    public void pay(final Database database, final int timestamp) {
        String oldNumber = this.getNumber();
        Account account = database.getCardNumberToAccount().get(getNumber());

        database.getCardNumberToAccount().remove(getNumber());
        this.setNumber(Utils.generateCardNumber());
        database.getCardNumberToAccount().put(getNumber(), account);

        for (User user : database.getUsers()) {
            if (user.getAccounts().contains(account)) {
                user.getTransactions().addLast(new DestroyedCard(timestamp,
                        oldNumber, user.getEmail(), account.getIban()));

                user.getTransactions().addLast(new CreateCardTransaction(timestamp,
                        this.getNumber(), user.getEmail(), account.getIban()));
            }
        }
    }
}
