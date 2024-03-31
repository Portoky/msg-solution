package validator;

import domain.*;
import repository.AccountsRepository;
import utils.MoneyUtils;

import java.time.LocalDate;
import java.util.List;

public class WithdrawValidator {
    public static void validate(TransactionModel transaction){
        AccountModel fromAccount = AccountsRepository.INSTANCE.get(transaction.getFrom());
        MoneyModel value = MoneyUtils.convert(transaction.getAmount(), fromAccount.getBalance().getCurrency());
        validateBalance(fromAccount, value);

        if(fromAccount.getAccountType().equals(AccountType.SAVINGS)){
            validateChecking(fromAccount, value, transaction.getTimestamp());
        }
        //for savings no restrictions
    }


    private static void validateBalance(AccountModel account, MoneyModel value){

        if (account.getBalance().getAmount() - value.getAmount() < 0) {
            throw new RuntimeException("There is not enough money from account " + account.getId());
        }
    }
    private static void validateChecking(AccountModel fromAccount, MoneyModel fromValue, LocalDate transactionDate){
        //validate if did not reach spend limit!
        fromValue = MoneyUtils.convert(fromValue, fromAccount.getBalance().getCurrency());
        List<TransactionModel> fromAccountTransactions = AccountsRepository.INSTANCE.get(fromAccount.getId()).getTransactions();
        double currentSumSoFar = fromAccountTransactions.stream().filter(transactionModel -> {return transactionModel.getFrom().equals(fromAccount.getId());})
                .filter(transactionModel -> {return transactionModel.getTimestamp().equals(transactionDate);})
                .mapToDouble(transactionModel -> { return MoneyUtils.convert(transactionModel.getAmount(), fromAccount.getBalance().getCurrency()).getAmount();})
                .sum();
        CheckingAccountModel checkingFromAccount = (CheckingAccountModel) fromAccount;
        if(currentSumSoFar + fromValue.getAmount() > checkingFromAccount.getAssociatedCard().getDailyTransactionLimit()){
            throw new RuntimeException("Transaction limit reached for card of Id: " + checkingFromAccount.getId());
        }
    }

}
