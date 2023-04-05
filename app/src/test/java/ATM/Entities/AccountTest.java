package ATM.Entities;

import ATM.Database.*;
import ATM.Entities.Accounts.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.junit.jupiter.api.Assertions.*;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.Mockito.*;
import java.sql.SQLException;

public class AccountTest {

    UserAccount user;

    ATMAccount atm;


    @BeforeEach
    public void reset() {
        user = new UserAccount("12345", 500);
        atm = new ATMAccount("12345", 10000);
    }

    /**
     * Basic User Account constructor test checking that required fields a set
     */
    @Test
    public void UserAccountConstructor() {
        UserAccount user = new UserAccount("123123123", 0);
        assertEquals("123123123", user.getId());
        assertEquals(0, user.getBalance());
        assertNotNull(user.getAccountDB());
    }

    /**
     * Basic ATM Account constructor test checking that required fields a set
     */
    @Test
    public void ATMAccountConstructor() {
        ATMAccount atm = new ATMAccount("123123123", 300);
        assertEquals("123123123", atm.getId());
        assertEquals(300, atm.getBalance());
        assertNotNull(atm.getAccountDB());
    }

    /**
     * Positive case checking that increasing the balance of an account by positive 200
     * always succeeds
     */
    @Test
    public void changeBalancePositive200Increment() throws SQLException {
        AccountDB db = mock(AccountDB.class);
        user = Mockito.spy(user);
        Mockito.doReturn(db).when(user).getAccountDB();
        doNothing().when(db).updateAccount(any(Account.class));
        assertTrue(user.changeBalance(200));
        assertEquals(700, user.getBalance());
    }

    /**
     * Positive case checking that increasing the balance of an account by positive 5
     * always succeeds
     */
    @Test
    public void changeBalancePositive5Increment() throws SQLException {
        AccountDB db = mock(AccountDB.class);
        user = Mockito.spy(user);
        Mockito.doReturn(db).when(user).getAccountDB();
        doNothing().when(db).updateAccount(any(Account.class));
        assertTrue(user.changeBalance(5));
        assertEquals(505, user.getBalance());
    }

    /**
     * Positive case checking that increasing the balance of an account by positive 10000
     * always succeeds
     */
    @Test
    public void changeBalancePositive10000Increment() throws SQLException {
        AccountDB db = mock(AccountDB.class);
        user = Mockito.spy(user);
        Mockito.doReturn(db).when(user).getAccountDB();
        doNothing().when(db).updateAccount(any(Account.class));
        assertTrue(user.changeBalance(10000));
        assertEquals(10500, user.getBalance());
    }

    /**
     * Negative case checking that overdrawing the balance by a small amount fails
     * and the balance is not changed
     */
    @Test
    public void changeBalanceSmallOverdraw() throws SQLException {
        assertFalse(user.changeBalance(-501));
        assertEquals(500, user.getBalance());
    }

    /**
     * Negative case checking that overdrawing the balance by a large amount fails
     * and the balance is not changed
     */
    @Test
    public void changeBalanceLargeOverdraw() throws SQLException {
        assertFalse(user.changeBalance(-777));
        assertEquals(500, user.getBalance());
    }

    /**
     * Edge case checking that if the balance change fails due to database
     * error the error is caught and the balance is not changed
     */
    @Test
    public void changeBalanceSQLExcepion() throws SQLException {
        AccountDB db = mock(AccountDB.class);
        user = Mockito.spy(user);
        Mockito.doReturn(db).when(user).getAccountDB();
        doThrow(SQLException.class).when(db).updateAccount(any(Account.class));

        assertFalse(user.changeBalance(10000));
        assertEquals(500, user.getBalance());
    }

    /**
     * Positive cases checking that an atm account object is returned when retrieving
     * an atm account from the database
     */
    @Test
    public void retrieveATMAccountTest() {
        AccountDB db = mock(AccountDB.class);
        ATMAccount atm = new ATMAccount("12345", 200);
        when(db.retrieveATMAccount("12345")).thenReturn(atm);
        Account.setDB(db);
        assertSame(atm, Account.retrieveAtmAccount("12345"));
        verify(db, times(1)).retrieveATMAccount("12345");

    }

    /**
     * Positive cases checking that an user account object is returned when retrieving
     * an user account from the database
     */
    @Test
    public void retrieveUserAccountTest() {
        AccountDB db = mock(AccountDB.class);
        UserAccount user = new UserAccount("12345", 200);
        when(db.retrieveUserAccount("12345")).thenReturn(user);
        Account.setDB(db);
        assertSame(user, Account.retrieveUserAccount("12345"));
        verify(db, times(1)).retrieveUserAccount("12345");

    }

}
