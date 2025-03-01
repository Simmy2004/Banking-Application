package org.poo.cashBackStrategies;

import org.poo.accounts.Account;
import org.poo.accounts.Discount;
import org.poo.solution.Commerciant;

import static org.poo.solution.Constants.*;

public final class NumberOfTransactionsStrategy implements CashBackStrategy {

    public NumberOfTransactionsStrategy() {
    }
    @Override
    public void getCashBack(final Account account, final Commerciant commerciant,
                            final double amount) {

        if (commerciant.getType().equals("Food")
                && account.getAvailableDiscounts().contains(Discount.FOOD)) {
            account.setBalance(account.getBalance() + amount * FOODRATE);
            account.getAvailableDiscounts().remove(Discount.FOOD);
            account.getUsedDiscounts().add(Discount.FOOD);
        } else if (commerciant.getType().equals("Tech")
                && account.getAvailableDiscounts().contains(Discount.TECH)) {
            account.setBalance(account.getBalance() + amount * TECHRATE);
            account.getAvailableDiscounts().remove(Discount.TECH);
            account.getUsedDiscounts().add(Discount.TECH);
        } else if (commerciant.getType().equals("Clothes")
                && account.getAvailableDiscounts().contains(Discount.CLOTHES)) {

            account.setBalance(account.getBalance() + amount * CLOTHESRATE);

            account.getAvailableDiscounts().remove(Discount.CLOTHES);
            account.getUsedDiscounts().add(Discount.CLOTHES);
        }
        updateDiscountsOnAccount(account, commerciant);
    }

    private void updateDiscountsOnAccount(final Account account, final Commerciant commerciant) {
        int payments = account.getPayToCommerciants().get(commerciant.getName());

        if (payments >= 2 && !account.getAvailableDiscounts().contains(Discount.FOOD)
                && !account.getUsedDiscounts().contains(Discount.FOOD)) {
            account.getAvailableDiscounts().add(Discount.FOOD);
        }

        if (payments >= 5 && !account.getAvailableDiscounts().contains(Discount.CLOTHES)
                && !account.getUsedDiscounts().contains(Discount.CLOTHES)) {
            account.getAvailableDiscounts().add(Discount.CLOTHES);
        }

        if (payments >= 10 && !account.getAvailableDiscounts().contains(Discount.TECH)
                && !account.getUsedDiscounts().contains(Discount.TECH)) {
            account.getAvailableDiscounts().add(Discount.TECH);
        }
    }
}
