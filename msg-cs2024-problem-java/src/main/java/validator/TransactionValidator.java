package validator;

import domain.*;
import repository.AccountsRepository;
import utils.MoneyUtils;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;

public class TransactionValidator {
    public static void validate(TransactionModel transaction) {
        AccountModel fromAccount = AccountsRepository.INSTANCE.get(transaction.getFrom());
        AccountModel toAccount = AccountsRepository.INSTANCE.get(transaction.getTo());

        validateAccountsType(fromAccount, toAccount);

        //converting so we pull the correct type of money from fromAccount
        MoneyModel fromValue = MoneyUtils.convert(transaction.getAmount(), fromAccount.getBalance().getCurrency());
        validateFromAccount(fromAccount, fromValue);

        validateFromAccountCard(fromAccount, fromValue, transaction.getTimestamp());

    }

    private static void validateAccountsType(AccountModel fromAccount, AccountModel toAccount){
        if (fromAccount.getAccountType() == AccountType.SAVINGS && toAccount.getAccountType() == AccountType.CHECKING) {
            throw new RuntimeException("Cannot make transfer: Savings Accounts => Checking Accounts");
        }

        if (fromAccount.getAccountType() == AccountType.SAVINGS && toAccount.getAccountType() == AccountType.SAVINGS) {
            throw new RuntimeException("Cannot make transfer: Savings Accounts => Savings Accounts");
        }
    }

    private static void validateFromAccount(AccountModel fromAccount, MoneyModel fromValue){
        if (fromAccount.getBalance().getAmount() - fromValue.getAmount() < 0) {
            throw new RuntimeException("There is not enough money from account " + fromAccount.getId());
        }
    }

    private static void validateFromAccountCard(AccountModel fromAccount, MoneyModel fromValue, LocalDate transactionDate){
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
