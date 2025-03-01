package org.poo.commands;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.accounts.Account;
import org.poo.solution.Database;

public class SetAlias implements Command {
    private Database database;
    private String email;
    private String alias;
    private String iban;
    private int timestamp;

    public SetAlias(final Database database, final String email, final String alias,
                    final String iban, final int timestamp) {
        this.database = database;
        this.email = email;
        this.alias = alias;
        this.iban = iban;
        this.timestamp = timestamp;
    }

    /**
     * Updates the database by adding possibility to find an account based on
     * the given alias.
     * @return This command always returns a null ObjectNode because no information
     * is requested about this process.
     */
    @Override
    public ObjectNode execute() {
        Account account = database.getIbanToAccount().get(iban);
        database.getAliasToAccount().put(alias, account);
        return null;
    }
}
