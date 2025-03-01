package org.poo.solution;

import lombok.Data;
import org.poo.accounts.Account;
import org.poo.fileio.UserInput;
import org.poo.commissionStrategies.CommissionStrategy;
import org.poo.commissionStrategies.GoldStudentStrategy;
import org.poo.commissionStrategies.StandardStrategy;
import org.poo.transactions.Transaction;

import java.util.ArrayList;

@Data
public final class User {
    private String firstName;
    private String lastName;
    private String email;
    private String birthDate;
    private String occupation;
    private ArrayList<Account> accounts;
    private ArrayList<Transaction> transactions;
    private ServicePlan servicePlan;
    private CommissionStrategy commissionStrategy;
    private int transactionsOverLimit;

    public User(final UserInput input) {
        firstName = input.getFirstName();
        lastName = input.getLastName();
        email = input.getEmail();
        accounts = new ArrayList<>();
        transactions = new ArrayList<>();
        occupation = input.getOccupation();
        birthDate = input.getBirthDate();
        if (occupation.equals("student")) {
            servicePlan = ServicePlan.STUDENT;
            commissionStrategy = new GoldStudentStrategy();
        } else {
            servicePlan = ServicePlan.STANDARD;
            commissionStrategy = new StandardStrategy();
        }
        transactionsOverLimit = 0;
    }

    /**
     * Calculates if the user's age is under 21.
     * @return True if the user is under the age of 21.
     */
    public boolean isAgeRestricted() {
        String[] parts = birthDate.split("-");
        int year = Integer.parseInt(parts[0]);
        int month = Integer.parseInt(parts[1]);
        int day = Integer.parseInt(parts[2]);

        if (year < 2004) {
            return false;
        } else if (year == 2004 && month == 1 && day < 1) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * Checks all the accounts of the user for a classic account type.
     * @return True if a classic account is found.
     */
    public boolean hasClassicAccount() {
        for (Account account : accounts) {
            if (account.getType().equals("classic")) {
                return true;
            }
        }
        return false;
    }



}
