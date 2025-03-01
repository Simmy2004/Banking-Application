package org.poo.solution;

import lombok.Data;
import org.poo.accounts.Account;
import org.poo.fileio.CommerciantInput;
import org.poo.fileio.UserInput;
import org.poo.splitPayments.SplitReport;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

@Data
public final class Database {
    private ArrayList<User> users;
    private ArrayList<Commerciant> commerciants;
    private Map<String, User> mailToUser = new HashMap<>();
    private Map<String, Account> ibanToAccount = new HashMap<>();
    private Map<String, Account> cardNumberToAccount = new HashMap<>();
    private Map<String, Account> aliasToAccount = new HashMap<>();
    private ArrayList<SplitReport> splits;

    public Database(final UserInput[] usersInputs, final CommerciantInput[] commerciantsInputs) {
        commerciants = new ArrayList<>();
        users = new ArrayList<>();
        splits = new ArrayList<>();

        for (UserInput userInput : usersInputs) {
            users.addLast(new User(userInput));

            User current = users.get(users.size() - 1);
            mailToUser.put(current.getEmail(), current);
        }

        for (CommerciantInput commerciantInput : commerciantsInputs) {
            commerciants.addLast(new Commerciant(commerciantInput));
        }

        commerciants.sort(Comparator.comparing(Commerciant::getName));
    }

    /**
     * Returns a commerciant based on the name given.
     * @param name The name of the commerciant
     * @return Commerciant class instance of the given commerciant name
     */
    public Commerciant getCommerciantByName(final String name) {
        for (Commerciant commerciant : commerciants) {
            if (commerciant.getName().equals(name)) {
                return commerciant;
            }
        }
        return null;
    }

}
