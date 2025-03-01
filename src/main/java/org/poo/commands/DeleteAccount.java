package org.poo.commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.accounts.Account;
import org.poo.accounts.Card;
import org.poo.solution.Database;
import org.poo.solution.User;
import org.poo.transactions.FundsRemaining;

public final class DeleteAccount implements Command {
    private Database database;
    private String email;
    private String iban;
    private int timestamp;

    public DeleteAccount(final int timestamp, final Database database,
                         final String email, final String iban) {
        this.database = database;
        this.email = email;
        this.iban = iban;
        this.timestamp = timestamp;
    }

    /**
     * Executes the DeleteAccount command. Removes the given account from the database
     * If the account's balance is not 0, it will not be affected. Adds the process
     * to the user history.
     * @return An ObjectNode containing information about errors if encountered, or
     * about the success of the command.
     */
    @Override
    public ObjectNode execute() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            User owner = database.getMailToUser().get(email);


            Account deletedAccount = database.getIbanToAccount().get(iban);
            if (owner == null || deletedAccount == null) {
                System.out.println("Account not found " + timestamp);
            }
            if (owner != deletedAccount.getOwner()) {
                System.out.println("Only the owner can delete accounts timestmap: " + timestamp);
                return null;
            }
            if (deletedAccount.getBalance() > 0) {

                ObjectNode mainNode = mapper.createObjectNode();
                mainNode.put("command", "deleteAccount");

                ObjectNode output = mapper.createObjectNode();
                output.put("error",
                        "Account couldn't be deleted - see org.poo.transactions for details");
                output.put("timestamp", timestamp);

                mainNode.put("output", output);
                mainNode.put("timestamp", timestamp);

                owner.getTransactions().addLast(new FundsRemaining(timestamp));

                return mainNode;
            }
            /* Removes all cards from database */
            for (Card card : deletedAccount.getCards()) {
                database.getCardNumberToAccount().remove(card.getNumber());
            }
            owner.getAccounts().remove(deletedAccount);
            database.getIbanToAccount().remove(iban);

        } catch (IllegalArgumentException e) {
            return null;
        }

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode mainObject = mapper.createObjectNode();
        mainObject.put("command", "deleteAccount");

        ObjectNode outputNode = mapper.createObjectNode();
        outputNode.put("success", "Account deleted");
        outputNode.put("timestamp", timestamp);

        mainObject.put("output", outputNode);
        mainObject.put("timestamp", timestamp);
        return mainObject;
    }
}
