import domain.CurrencyType;
import domain.MoneyModel;
import domain.TransactionModel;
import org.junit.Test;
import seed.SeedInitializer;
import services.SavingsManagerService;
import services.TransactionManagerService;

import static org.junit.Assert.*;
import static seed.AccountsSeedData.*;

public class BankingApplicationTest {

    @Test
    public void transactionRONtoRONTest(){
        SeedInitializer.seedData();
        TransactionManagerService transactionManagerServiceInstance = new TransactionManagerService();
        assertEquals(checkingAccountA.getBalance().getAmount(), new MoneyModel(100, CurrencyType.RON).getAmount(), 0.0);
        assertEquals(checkingAccountB.getBalance().getAmount(), new MoneyModel(300, CurrencyType.RON).getAmount(), 0.0);
        TransactionModel transaction1 = transactionManagerServiceInstance.transfer(
                checkingAccountA.getId(),
                checkingAccountB.getId(),
                new MoneyModel(100, CurrencyType.RON)
        );
        assertEquals(checkingAccountA.getBalance().getAmount(), new MoneyModel(0, CurrencyType.RON).getAmount(), 0.0);
        assertEquals(checkingAccountB.getBalance().getAmount(), new MoneyModel(400, CurrencyType.RON).getAmount(), 0.0);
    }

    @Test
    public void transactionEURtoEURTest(){
        SeedInitializer.seedData();
        TransactionManagerService transactionManagerServiceInstance = new TransactionManagerService();
        assertEquals(checkingAccountC.getBalance().getAmount(), new MoneyModel(10, CurrencyType.EUR).getAmount(), 0.0);
        assertEquals(checkingAccountD.getBalance().getAmount(), new MoneyModel(1000, CurrencyType.EUR).getAmount(), 0.0);
        TransactionModel transaction1 = transactionManagerServiceInstance.transfer(
                checkingAccountC.getId(),
                checkingAccountD.getId(),
                new MoneyModel(5, CurrencyType.EUR)
        );
        assertEquals(checkingAccountC.getBalance().getAmount(), new MoneyModel(5, CurrencyType.EUR).getAmount(), 0.0);
        assertEquals(checkingAccountD.getBalance().getAmount(), new MoneyModel(1005, CurrencyType.EUR).getAmount(), 0.0);
    }

    @Test
    public void transactionEURtoRONTest() {
        SeedInitializer.seedData();
        TransactionManagerService transactionManagerServiceInstance = new TransactionManagerService();
        assertEquals(checkingAccountC.getBalance().getAmount(), new MoneyModel(10, CurrencyType.EUR).getAmount(), 0.0);
        assertEquals(checkingAccountB.getBalance().getAmount(), new MoneyModel(300, CurrencyType.RON).getAmount(), 0.0);

        TransactionModel transaction1 = transactionManagerServiceInstance.transfer(
                checkingAccountC.getId(),
                checkingAccountB.getId(),
                new MoneyModel(20, CurrencyType.RON)
        );
        assertEquals(checkingAccountC.getBalance().getAmount(), new MoneyModel(6, CurrencyType.EUR).getAmount(), 0.0);
        assertEquals(checkingAccountB.getBalance().getAmount(), new MoneyModel(320, CurrencyType.RON).getAmount(), 0.0);
    }

    @Test
    public void transactionRONtoEURTest() {
        SeedInitializer.seedData();
        TransactionManagerService transactionManagerServiceInstance = new TransactionManagerService();
        assertEquals(checkingAccountB.getBalance().getAmount(), new MoneyModel(300, CurrencyType.RON).getAmount(), 0.0);
        assertEquals(checkingAccountC.getBalance().getAmount(), new MoneyModel(10, CurrencyType.EUR).getAmount(), 0.0);


        TransactionModel transaction1 = transactionManagerServiceInstance.transfer(
                checkingAccountB.getId(),
                checkingAccountC.getId(),
                new MoneyModel(20, CurrencyType.RON)
        );
        assertEquals(checkingAccountB.getBalance().getAmount(), new MoneyModel(280, CurrencyType.RON).getAmount(), 0.0);
        assertEquals(checkingAccountC.getBalance().getAmount(), new MoneyModel(14, CurrencyType.EUR).getAmount(), 0.0);

    }

    @Test
    public void withdrawTest(){
        SeedInitializer.seedData();
        TransactionManagerService transactionManagerServiceInstance = new TransactionManagerService();
        transactionManagerServiceInstance.withdraw(
                checkingAccountC.getId(),
                new MoneyModel(5, CurrencyType.EUR)
        );
    }

    @Test
    public  void accountTypeTest(){
        SeedInitializer.seedData();
        TransactionManagerService transactionManagerServiceInstance = new TransactionManagerService();
        try {
            TransactionModel transaction1 = transactionManagerServiceInstance.transfer(
                    savingsAccountA.getId(),
                    checkingAccountC.getId(),
                    new MoneyModel(20, CurrencyType.RON)
            );
        }
        catch (RuntimeException e){
            assertNotNull(e);
            return;
        }
        fail("Account Type test failed");
    }

    @Test
    public void notEnoughMoneyTransferTest(){
        SeedInitializer.seedData();
        TransactionManagerService transactionManagerServiceInstance = new TransactionManagerService();
        try {
            transactionManagerServiceInstance.withdraw(
                    checkingAccountC.getId(),
                    new MoneyModel(60, CurrencyType.RON)
            );
        }
        catch (RuntimeException e){
            assertNotNull(e);
            return;
        }
        fail("Account Type test failed");
    }

    @Test
    public void notEnoughMoneyWithdrawTest(){
        SeedInitializer.seedData();
        TransactionManagerService transactionManagerServiceInstance = new TransactionManagerService();
        try {
            TransactionModel transaction1 = transactionManagerServiceInstance.transfer(
                    savingsAccountA.getId(),
                    checkingAccountC.getId(),
                    new MoneyModel(1100, CurrencyType.RON)
            );
        }
        catch (RuntimeException e){
            assertNotNull(e);
            return;
        }
        fail("Account Type test failed");
    }

    @Test
    public void saving1MonthTest(){
        SeedInitializer.seedData();
        SavingsManagerService savingsManagerServiceInstance = new SavingsManagerService();
        savingsManagerServiceInstance.passTime();
        assertEquals(savingsAccountA.getBalance().getAmount(), 1055,0.0);
        savingsManagerServiceInstance.passTime();
        assertEquals(savingsAccountA.getBalance().getAmount(), 1113.025,0.0);
    }

    @Test
    public void savingQuarterTest(){
        SeedInitializer.seedData();
        SavingsManagerService savingsManagerServiceInstance = new SavingsManagerService();
        savingsManagerServiceInstance.passTime();
        assertEquals(savingsAccountB.getBalance().getAmount(), 2000,0.0);
        savingsManagerServiceInstance.passTime();
        assertEquals(savingsAccountB.getBalance().getAmount(), 2000,0.0);
        savingsManagerServiceInstance.passTime();
        assertEquals(savingsAccountB.getBalance().getAmount(), 2113,0.0);
    }
}
