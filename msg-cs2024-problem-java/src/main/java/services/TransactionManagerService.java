package services;

import domain.AccountModel;
import domain.MoneyModel;
import domain.TransactionModel;
import repository.AccountsRepository;
import utils.MoneyUtils;
import validator.TransactionValidator;
import validator.WithdrawValidator;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TransactionManagerService {

    public TransactionModel transfer(String fromAccountId, String toAccountId, MoneyModel value) {
        AccountModel fromAccount = AccountsRepository.INSTANCE.get(fromAccountId);
        AccountModel toAccount = AccountsRepository.INSTANCE.get(toAccountId);

        if (fromAccount == null || toAccount == null) {
            throw new RuntimeException("Specified account does not exist");
        }

        MoneyModel toValue = MoneyUtils.convert(value, toAccount.getBalance().getCurrency()); //so the final currency is the destination currency
        MoneyModel fromValue = MoneyUtils.convert(value, fromAccount.getBalance().getCurrency()); //we still need this type of currency as well

        TransactionModel transaction = new TransactionModel(
                UUID.randomUUID(),
                fromAccountId,
                toAccountId,
                toValue, //final currencyType
                LocalDate.now()
        );
        //validation
        TransactionValidator.validate(transaction);


        //validated, we can make the transaction with converting
        fromAccount.getBalance().setAmount(fromAccount.getBalance().getAmount() - fromValue.getAmount()); //correcting withdrawal to the correct money type
        fromAccount.getTransactions().add(transaction);

        toAccount.getBalance().setAmount(toAccount.getBalance().getAmount() + toValue.getAmount()); //correct money type added to account
        toAccount.getTransactions().add(transaction);

        return transaction;
    }

    public TransactionModel withdraw(String accountId, MoneyModel amount) {
        AccountModel account = AccountsRepository.INSTANCE.get(accountId);

        if(account == null){
            throw new RuntimeException("Specified account does not exist");
        }

        MoneyModel toValue = MoneyUtils.convert(amount, account.getBalance().getCurrency());

        TransactionModel transaction = new TransactionModel(
                UUID.randomUUID(),
                account.getId(),
                "NULL", //withdrawal!
                toValue, //final currencyType
                LocalDate.now()
        );

        WithdrawValidator.validate(transaction);

        account.getBalance().setAmount(account.getBalance().getAmount() - amount.getAmount());
        account.getTransactions().add(transaction);

        return transaction;
    }

    public MoneyModel checkFunds(String accountId) {
        if (!AccountsRepository.INSTANCE.exist(accountId)) {
            throw new RuntimeException("Specified account does not exist");
        }
        return AccountsRepository.INSTANCE.get(accountId).getBalance();
    }

    public List<TransactionModel> retrieveTransactions(String accountId) {
        if (!AccountsRepository.INSTANCE.exist(accountId)) {
            throw new RuntimeException("Specified account does not exist");
        }
        return new ArrayList<>(AccountsRepository.INSTANCE.get(accountId).getTransactions());
    }
}

