package org.poo.cashBackStrategies;

import org.poo.accounts.Account;
import org.poo.accounts.Discount;
import org.poo.solution.Commerciant;
import org.poo.solution.ServicePlan;
import org.poo.weightedGraph.ExchangeGraph;

import static org.poo.solution.Constants.*;

public final class SpendingThresholdStrategy implements CashBackStrategy {

    public SpendingThresholdStrategy() {

    }
    @Override
    public void getCashBack(final Account account, final Commerciant commerciant,
                            final double amount) {
        double amountInRON = ExchangeGraph.getInstance(null).
                getRateFromTo(account.getCurrency(), "RON") * amount;

        double amountToCompare = 0;
        account.setSpentOnCommerciants(amountInRON + account.getSpentOnCommerciants());
        amountToCompare = account.getSpentOnCommerciants();

        ServicePlan plan = account.getOwner().getServicePlan();

        switch (plan) {
            case STANDARD, STUDENT -> {
                if (amountToCompare >= HIGHCAP) {
                    account.setBalance(account.getBalance() + STANDARDHIGH * amount);
                } else if (amountToCompare >= MIDCAP) {
                    account.setBalance(account.getBalance() + STANDARDMID * amount);
                } else if (amountToCompare >= LOWCAP) {
                    account.setBalance(account.getBalance() + STANDARDLOW * amount);
                }

            }
            case SILVER -> {
                if (amountToCompare >= HIGHCAP) {
                    account.setBalance(account.getBalance() + SILVERHIGH * amount);
                } else if (amountToCompare >= MIDCAP) {
                    account.setBalance(account.getBalance() + SILVERMID * amount);
                } else if (amountToCompare >= LOWCAP) {
                    account.setBalance(account.getBalance() + SILVERLOW * amount);
                }

            }
            case GOLD -> {
                if (amountToCompare >= HIGHCAP) {
                    account.setBalance(account.getBalance() + GOLDHIGH * amount);
                } else if (amountToCompare >= MIDCAP) {
                    account.setBalance(account.getBalance() + GOLDMID * amount);
                } else if (amountToCompare >= LOWCAP) {
                    account.setBalance(account.getBalance() + GOLDLOW * amount);
                }
            }
            default -> {
                break;
            }
        }

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
    }
}
