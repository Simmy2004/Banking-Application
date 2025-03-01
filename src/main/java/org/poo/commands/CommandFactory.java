package org.poo.commands;

import lombok.Data;
import org.poo.fileio.CommandInput;
import org.poo.solution.Database;

@Data
public class CommandFactory {

    private final Database database;

    public CommandFactory(final Database database) {
        this.database = database;
    }

    /**
     * Factory design pattern. Return a specific type of command object depending
     * on the request given.
     * @param command Input containing all the requests.
     * @return Needed command type of object for the given request.
     */
    public Command getCommand(final CommandInput command) {
        return switch (command.getCommand()) {
            case "printUsers" -> new PrintUsers(command.getTimestamp(), database);

            case "addAccount" -> new AddAccount(command.getTimestamp(), database,
                    command.getEmail(), command.getCurrency(), command.getAccountType(),
                    command.getInterestRate());

            case "createCard" -> new CreateCard(command.getTimestamp(), database,
                    command.getAccount(), command.getEmail());

            case "addFunds" -> new AddFunds(command.getTimestamp(), database,
                    command.getAmount(), command.getAccount(), command.getEmail());

            case "deleteAccount" -> new DeleteAccount(command.getTimestamp(), database,
                    command.getEmail(), command.getAccount());

            case "createOneTimeCard" -> new CreateOneTimeCard(command.getTimestamp(), database,
                    command.getAccount(), command.getEmail());

            case "deleteCard" -> new DeleteCard(command.getTimestamp(), database,
                    command.getCardNumber(), command.getEmail());

            case "setMinimumBalance" -> new SetMinimumBalance(command.getTimestamp(), database,
                    command.getMinBalance(), command.getAccount());

            case "payOnline" -> new PayOnline(command.getCardNumber(), command.getAmount(),
                    command.getCurrency(), command.getTimestamp(), command.getDescription(),
                    command.getCommerciant(), command.getEmail(), database);

            case "sendMoney" -> new SendMoney(command.getAccount(), command.getReceiver(),
                    command.getAmount(), command.getEmail(), command.getDescription(),
                    database, command.getTimestamp());

            case "printTransactions" -> new PrintTransactions(database.getMailToUser().
                    get(command.getEmail()), command.getTimestamp());

            case "setAlias" -> new SetAlias(database, command.getEmail(), command.getAlias(),
                    command.getAccount(), command.getTimestamp());

            case "checkCardStatus" -> new CheckCardStatus(command.getTimestamp(),
                    command.getCardNumber(), database);

            case "splitPayment" ->  {
                if (command.getSplitPaymentType().equals("equal")) {
                    yield new EqualSplitPayment(command.getAccounts(), command.getAmount(),
                            command.getCurrency(), command.getTimestamp(), database);
                } else {
                    yield new CustomSplitPayment(command.getAccounts(), command.getAmount(),
                            command.getAmountForUsers(), command.getCurrency(),
                            command.getTimestamp(), database);
                }
            }

            case "report" -> new Report(command.getStartTimestamp(), command.getEndTimestamp(),
                    command.getAccount(), command.getTimestamp(), database);

            case "spendingsReport" -> new SpendingsReport(command.getStartTimestamp(),
                    command.getEndTimestamp(), command.getAccount(),
                    command.getTimestamp(), database);

            case "changeInterestRate" -> new ChangeInterest(command.getTimestamp(),
                    command.getAccount(), command.getInterestRate(), database);

            case "addInterest" -> new AddInterest(command.getTimestamp(),
                    command.getAccount(), database);

            case "withdrawSavings" -> new WithdrawSavings(command.getAccount(), command.getAmount(),
                    command.getCurrency(), command.getTimestamp(), database);

            case "upgradePlan" -> new UpgradePlan(command.getNewPlanType(), command.getAccount(),
                    command.getTimestamp(), database);

            case "cashWithdrawal" -> new CashWithdraw(command.getCardNumber(), command.getAmount(),
                    command.getEmail(), command.getLocation(), command.getTimestamp(), database);

            case "acceptSplitPayment" -> new AcceptSplit(command.getEmail(),
                    command.getSplitPaymentType(), command.getTimestamp(), database);

            case "addNewBusinessAssociate" -> new AddBusinessAssociate(command.getAccount(),
                    command.getRole(), command.getEmail(), command.getTimestamp(), database);

            case "changeSpendingLimit" -> new ChangeSpendingLimit(command.getEmail(), database,
                    command.getAccount(), command.getAmount(), command.getTimestamp());

            case "changeDepositLimit" -> new ChangeDepositLimit(command.getEmail(), database,
                    command.getAccount(), command.getAmount(), command.getTimestamp());

            case "businessReport" -> new BusinessReport(command.getStartTimestamp(),
                    command.getEndTimestamp(), command.getTimestamp(), command.getAccount(),
                    command.getType(), database);

            case "rejectSplitPayment" -> new RejectSplit(command.getEmail(),
                    command.getSplitPaymentType(), command.getTimestamp(), database);
            default -> null;
        };


    }

}
