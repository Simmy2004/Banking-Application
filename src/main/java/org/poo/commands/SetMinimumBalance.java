package org.poo.commands;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.accounts.Account;
import org.poo.solution.Database;

public class SetMinimumBalance implements Command {
    private int timestamp;
    private Database database;
    private double minimumBalance;
    private String iban;

    public SetMinimumBalance(final int timestamp, final Database database,
                             final double minimumBalance, final String iban) {
        this.timestamp = timestamp;
        this.database = database;
        this.minimumBalance = minimumBalance;
        this.iban = iban;
    }

    /**
     * Executes the setMinimumBalance command. Sets a threshold for the minimum
     * accepted balance on a card before it gets frozen and no more transaction will
     * be permitted.
     * @return This command always return a null ObjectNode on execution because
     * no information about this process is requested.
     */
    @Override
    public ObjectNode execute() {
        try {
            Account account = database.getIbanToAccount().get(iban);
            if (account == null) {
                throw new IllegalArgumentException("Invalid IBAN");
            }
            /* Set on 0 at start */
            account.setMinimumBalance(minimumBalance);
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid IBAN");
            e.printStackTrace();
        }
        return null;
    }
}
