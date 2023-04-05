package ATM.Entities;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.sql.SQLException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import ATM.Entities.Accounts.ATMAccount;
import ATM.Entities.Accounts.Account;
import ATM.Entities.Accounts.UserAccount;
import ATM.Entities.Transactions.*;
import ATM.utils.ATMException;
import ATM.Database.*;

public class TransactionTest {

    public UserAccount userAcc;
    public ATMAccount atmAcc;
    public AccountDB adb;
    public TransactionDB tdb;

    @BeforeEach
    public void setup() {
        this.userAcc = mock(UserAccount.class);
        when(userAcc.getId()).thenReturn("5");

        this.atmAcc = mock(ATMAccount.class);
        when(atmAcc.getId()).thenReturn("1");

        this.adb = mock(AccountDB.class);
        this.tdb = mock(TransactionDB.class);
    }

    /**
     * Basic Deposit constructor test checking all required fields are set
     */
    @Test
    public void testDepositConstructor() {
        Deposit deposit = new Deposit(this.userAcc, this.atmAcc, 500);
        assertEquals("5", deposit.getUserAccount().getId());
        assertEquals("1", deposit.getAtmAccount().getId());
        assertEquals(500, deposit.getQuantity());
        assertEquals("Deposit", deposit.getTypeStr());
    }

    /**
     * Basic Withdrawal constructor test checking all required fields are set
     */
    @Test
    public void testWithdrawalConstructor() {
        Withdrawal withdrawal = new Withdrawal(this.userAcc, this.atmAcc, 500);
        assertEquals("5", withdrawal.getUserAccount().getId());
        assertEquals("1", withdrawal.getAtmAccount().getId());
        assertEquals(500, withdrawal.getQuantity());
        assertEquals("Withdrawal", withdrawal.getTypeStr());
    }

    /**
     * Basic Balance constructor test checking all required fields are set
     */
    @Test
    public void testBalanceConstructorNoDB() {
        Balance balance = new Balance(this.userAcc, this.atmAcc, 500);
        assertEquals("5", balance.getUserAccount().getId());
        assertEquals("1", balance.getAtmAccount().getId());
        assertEquals(500, balance.getQuantity());
        assertEquals("Balance", balance.getTypeStr());
    }

    /**
     * Positive case checking that withdrawing creates an attempt
     * @throws ATMException
     */
    @Test
    public void testWithdrawAttemptSuccess() throws ATMException {
        float quantity = 500;
        Withdrawal withdrawal = Mockito.spy(new Withdrawal(this.userAcc, this.atmAcc, quantity));
        doReturn(false).when(withdrawal).isUserTransactionATM();
        doReturn(true).when(withdrawal).recordTransaction();
        when(this.userAcc.getBalance()).thenReturn(1000.0);
        when(this.atmAcc.getBalance()).thenReturn(1500.0);
        when(this.atmAcc.changeBalance(-quantity)).thenReturn(true);
        when(this.userAcc.changeBalance(-quantity)).thenReturn(true);
        assertTrue(withdrawal.attempt());
    }

    /**
     * Negative case checking that withdrawing fails if the ATM account cannot change balance
     * @throws ATMException
     */
    @Test
    public void testWithdrawAttemptFailEdge() throws ATMException {
        float quantity = 500;
        Withdrawal withdrawal = Mockito.spy(new Withdrawal(this.userAcc, this.atmAcc, quantity));
        doReturn(false).when(withdrawal).isUserTransactionATM();
        doReturn(true).when(withdrawal).recordTransaction();
        when(this.userAcc.getBalance()).thenReturn(1000.0);
        when(this.atmAcc.getBalance()).thenReturn(1500.0);
        when(this.atmAcc.changeBalance(-quantity)).thenReturn(false);
        when(this.userAcc.changeBalance(-quantity)).thenReturn(true);
        assertFalse(withdrawal.attempt());
    }

    /**
     * Negative case checking that overdrawing the balance of the ATM results in a failed withdrawal
     * @throws ATMException
     */
    @Test
    public void testWithdrawAttemptFailOverdrawATM() throws ATMException {
        float quantity = 2500;
        Withdrawal withdrawal = Mockito.spy(new Withdrawal(this.userAcc, this.atmAcc, quantity));
        doReturn(false).when(withdrawal).isUserTransactionATM();
        doReturn(true).when(withdrawal).recordTransaction();
        when(this.userAcc.getBalance()).thenReturn(5000.0);
        when(this.atmAcc.getBalance()).thenReturn(100.0);

        Exception exception = assertThrows(ATMException.class, withdrawal::attempt);
        String expectedMessage = "ATM Machine is overdrawn. Please refill the machine to withdraw money.";
        String actualMessage = exception.getMessage();
        assertEquals(actualMessage, expectedMessage);
    }

    /**
     * Negative case checking that overdrawing the balance of the user account results in the correct error message
     * @throws ATMException
     */
    @Test
    public void testWithdrawAttemptFailOverdrawUser() throws ATMException {
        float quantity = 2500;
        Withdrawal withdrawal = Mockito.spy(new Withdrawal(this.userAcc, this.atmAcc, quantity));
        doReturn(false).when(withdrawal).isUserTransactionATM();
        when(this.userAcc.getBalance()).thenReturn(100.0);
        when(this.atmAcc.getBalance()).thenReturn(5000.0);
        Exception exception = assertThrows(ATMException.class, withdrawal::attempt);
        String expectedMessage = "Not enough money in your account to withdrawn this amount.";
        String actualMessage = exception.getMessage();
        assertEquals(actualMessage, expectedMessage);
    }

    /**
     * Negative case checking that ATM exception is thrown when ATM account is provided as user
     */
    @Test
    public void testWithdrawAttemptATMAccountAsUser() {
        float quantity = 2500;
        Withdrawal withdrawal = Mockito.spy(new Withdrawal(this.userAcc, this.atmAcc, quantity));
        doReturn(true).when(withdrawal).isUserTransactionATM();
        when(this.userAcc.getBalance()).thenReturn(10000.0);
        when(this.atmAcc.getBalance()).thenReturn(10000.0);
        try {
            withdrawal.attempt();
            fail();
        } catch (ATMException e) {
            assertTrue(true);
        }
    }

    /**
     * Negative case testing a withdrawal request of a zero amount returns correct error message
     * @throws ATMException
     */
    @Test
    public void testWithdrawAttemptFailZero() throws ATMException {
        float quantity = 0;
        Withdrawal withdrawal = Mockito.spy(new Withdrawal(this.userAcc, this.atmAcc, quantity));
        doReturn(false).when(withdrawal).isUserTransactionATM();
        Exception exception = assertThrows(ATMException.class, withdrawal::attempt);
        String expectedMessage = "Cannot withdraw negative $";
        String actualMessage = exception.getMessage();
        assertEquals(actualMessage, expectedMessage);
    }

    /**
     * Negative case testing a withdrawal request of a negative amount returns correct error message
     * @throws ATMException
     */
    @Test
    public void testWithdrawAttemptFailUnderdraw() throws ATMException {
        float quantity = -5;
        Withdrawal withdrawal = new Withdrawal(this.userAcc, this.atmAcc, quantity);


        Exception exception = assertThrows(ATMException.class, withdrawal::attempt);
        String expectedMessage = "Cannot withdraw negative $";
        String actualMessage = exception.getMessage();
        assertEquals(actualMessage, expectedMessage);
    }

    /**
     * Positive case checking that deposit successfully creates an attempt
     * @throws ATMException
     */
    @Test
    public void testDepositAttemptSuccess() throws ATMException {
        float quantity = 500;
        Deposit deposit = Mockito.spy(new Deposit(this.userAcc, this.atmAcc, quantity));
        doReturn(true).when(deposit).recordTransaction();
        when(this.userAcc.getBalance()).thenReturn(1000.0);
        when(this.atmAcc.getBalance()).thenReturn(1500.0);
        when(this.atmAcc.changeBalance(anyDouble())).thenReturn(true);
        when(this.userAcc.changeBalance(anyDouble())).thenReturn(true);
        assertTrue(deposit.attempt());
    }

    /**
     * Negative case testing when the User account cannot change balance, the attempt isn't successful
     * @throws ATMException
     */
    @Test
    public void testDepositAttemptFailEdge() throws ATMException {
        float quantity = 500;
        Deposit deposit = new Deposit(this.userAcc, this.atmAcc, quantity);
        when(this.userAcc.getBalance()).thenReturn(1000.0);
        when(this.atmAcc.getBalance()).thenReturn(1500.0);
        when(this.atmAcc.changeBalance(quantity)).thenReturn(true);
        when(this.userAcc.changeBalance(quantity)).thenReturn(false);
        assertFalse(deposit.attempt());
    }

    /**
     * Negative case testing that a requested 0 amount creates the correct error message
     */
    @Test
    public void testDepositAttemptFailZero() {
        float quantity = 0;
        Deposit deposit = new Deposit(this.userAcc, this.atmAcc, quantity);

        Exception exception = assertThrows(ATMException.class, deposit::attempt);

        String expectedMessage = "Cannot deposit negative $";
        String actualMessage = exception.getMessage();
        assertEquals(actualMessage, expectedMessage);
    }

    /**
     * Negative case testing that a requested negative amount creates the correct error message
     */
    @Test
    public void testDepositAttemptFailNegative() {
        float quantity = -5;
        Deposit deposit = new Deposit(this.userAcc, this.atmAcc, quantity);
        Exception exception = assertThrows(ATMException.class, deposit::attempt);

        String expectedMessage = "Cannot deposit negative $";
        String actualMessage = exception.getMessage();
        assertEquals(actualMessage, expectedMessage);
    }


    /**
     * Positive case testing that a balance can be produced correctly
     */
    @Test
    public void testBalanceAttemptSuccess() {
        float quantity = 0;
        Balance balance = Mockito.spy(new Balance(this.userAcc, this.atmAcc, quantity));
        when(this.userAcc.getBalance()).thenReturn(150.0);
        doReturn(true).when(balance).recordTransaction();
        assertTrue(balance.attempt());
        assertEquals(150.0, balance.getQuantity());
    }

    /**
     * Negative case testing that transaction doesn't occur with incorrect user account
     */
    @Test
    public void testIsUserTransactionPositive() {
        UserAccount user = mock(UserAccount.class);
        when(user.getId()).thenReturn("Not an atm id");
        ATMAccount atm = mock(ATMAccount.class);
        Transaction t = new Deposit(user, atm, 999);
        assertFalse(t.isUserTransactionATM());
    }

    /**
     * Negative case testing that transaction doesn't occur without a user Account
     */
    @Test
    public void testIsUserTransactionATMNoUserAccount() {
        ATMAccount atm = mock(ATMAccount.class);
        Transaction t = new Deposit(null, atm, 999);
        assertFalse(t.isUserTransactionATM());
    }

    /**
     * Positive test case testing matching User and ATM id's creating a successfull transaction
     */
    @Test
    public void testIsUserTransactionATMMatchingATMID() {
        UserAccount user = mock(UserAccount.class);
        when(user.getId()).thenReturn(Account.ATM_ACCOUNT_ID);
        ATMAccount atm = mock(ATMAccount.class);
        Transaction t = new Deposit(user, atm, 999);
        assertTrue(t.isUserTransactionATM());
    }

}
