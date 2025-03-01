package org.poo.accounts;

import lombok.Data;
import org.poo.solution.User;

@Data
public final class SavingsAccount extends Account {
    private double interest;

    public SavingsAccount(final String currency, final User user, final double interest) {
        super(currency, user);
        this.interest = interest;
    }

    @Override
    public String getType() {
        return "savings";
    }
}
