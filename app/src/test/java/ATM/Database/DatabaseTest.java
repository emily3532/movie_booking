//package ATM.Database;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//import ATM.Entities.Accounts.ATMAccount;
//import ATM.Entities.Accounts.Account;
//import ATM.Entities.Accounts.UserAccount;
//import ATM.Entities.Card;
//import ATM.Entities.Transactions.Deposit;
//import ATM.Entities.Transactions.Transaction;
//import org.checkerframework.checker.units.qual.A;
//import org.checkerframework.checker.units.qual.C;
//
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
////import org.mockito.MockedStatic;
////import org.mockito.Mockito;
//
//import ATM.Database.*;
//import org.mockito.Mock;
//import org.mockito.Mockito;
//
//import javax.xml.crypto.Data;
//import java.io.ByteArrayOutputStream;
//import java.io.PrintStream;
//import java.sql.*;
//import java.time.LocalDate;
//import java.time.format.DateTimeFormatter;
//import java.util.ArrayList;
//import java.util.List;
//
//
//
//public class DatabaseTest {
//
//    @Mock
//    private Connection connection;
//
//    @Mock
//    private ResultSet rs;
//
//    @Mock
//    private Statement statement;
//
//    @BeforeEach
//    public void setup() throws SQLException {
//        this.connection = mock(Connection.class);
//        this.statement = mock(Statement.class);
//        this.rs = mock(ResultSet.class);
//        when(connection.createStatement()).thenReturn(statement);
//    }
//
//    /**
//     * Positive case testing correct querying to the database to add an account
//     * @throws SQLException
//     */
//    @Test
//    public void insertAccountTest() throws SQLException {
//        AccountDB accDB = Mockito.spy(new AccountDB());
//        when(accDB.getConnection()).thenReturn(connection);
//        accDB.insertAccount("10000000", 100.0);
//        verify(accDB, times(1)).modify("insert into account values (10000000, 100.0);");
//    }
//
//    /**
//     * Positive case testing correct querying to database to delete an account
//     * @throws SQLException
//     */
//    @Test
//    public void deleteAccountTest() throws SQLException {
//        AccountDB accDB = Mockito.spy(new AccountDB());
//        when(accDB.getConnection()).thenReturn(connection);
//        accDB.deleteAccount("10000000");
//        verify(accDB, times(1)).modify("delete from account where accountNum = '10000000'");
//    }
//
//    /**
//     * Positive case testing correct querying to database to update an account
//     * @throws SQLException
//     */
//    @Test
//    public void updateAccountTest() throws SQLException {
//        AccountDB accDB = Mockito.spy(new AccountDB());
//        Account acc = mock(Account.class);
//        when(accDB.getConnection()).thenReturn(connection);
//        when(acc.getBalance()).thenReturn(100.0);
//        when(acc.getId()).thenReturn("10000000");
//        accDB.updateAccount(acc);
//        verify(accDB, times(1)).modify("UPDATE account\n" +
//                "SET balance = '100.0'\n" +
//                "WHERE accountNum = '10000000'");
//    }
//
//    /**
//     * Positive case testing retrieval of account from database, checking for correct User Account properties and querying
//     * @throws SQLException
//     */
//    @Test
//    public void testAccountDBRetrieveUserAccount() throws SQLException {
//        AccountDB accDB = Mockito.spy(new AccountDB());
//        when(accDB.getConnection()).thenReturn(connection);
//        when(statement.executeQuery("select accountNum, balance from account where accountNum = '10000000'")).thenReturn(rs);
//        when(rs.isBeforeFirst()).thenReturn(true);
//        doReturn(rs).when(accDB).select("select accountNum, balance from account where accountNum = '10000000'");
//        when(rs.next()).thenReturn(true);
//        when(rs.getString("accountNum")).thenReturn("10000000");
//        when(rs.getDouble("balance")).thenReturn(100.0);
//        assertEquals(100.0, accDB.retrieveUserAccount("10000000").getBalance());
//        assertSame("10000000", accDB.retrieveUserAccount("10000000").getId());
//    }
//
//    /**
//     * Positive case testing retrieval ATM account from database, checking for correct ATM Account properties and querying
//     * @throws SQLException
//     */
//    @Test
//    public void testAccountDBRetrieveATMAccount() throws SQLException {
//        AccountDB accDB = Mockito.spy(new AccountDB());
//        when(accDB.getConnection()).thenReturn(connection);
//        when(statement.executeQuery("select accountNum, balance from account where accountNum = '10000000'")).thenReturn(rs);
//        when(rs.isBeforeFirst()).thenReturn(true);
//        doReturn(rs).when(accDB).select("select accountNum, balance from account where accountNum = '10000000'");
//        when(rs.next()).thenReturn(true);
//        when(rs.getString("accountNum")).thenReturn("10000000");
//        when(rs.getDouble("balance")).thenReturn(100.0);
//        assertEquals(100.0, accDB.retrieveATMAccount("10000000").getBalance());
//        assertSame("10000000", accDB.retrieveATMAccount("10000000").getId());
//    }
//
//
//    /**
//     * Positive case testing querying for the insertion of a Card instance into the DB
//     * @throws SQLException
//     */
//    @Test
//    public void insertCardTest() throws SQLException {
//        CardDB cardDb = Mockito.spy(new CardDB());
//        when(cardDb.getConnection()).thenReturn(connection);
//        cardDb.insertCard("10000000", "0000", "08/21", "08/24");
//        verify(cardDb, times(1)).modify("insert into Card values (10000000, 0000, '08/21', '08/24', 'OK');");
//    }
//
//    /**
//     * Positive case testing the querying of linking an account to a card to the Card_Account table in DB
//     * @throws SQLException
//     */
//    @Test
//    public void linkCardAccountTest() throws SQLException {
//        CardDB cardDb = Mockito.spy(new CardDB());
//        when(cardDb.getConnection()).thenReturn(connection);
//        cardDb.linkCardAccount("10000000", "10000000");
//        verify(cardDb, times(1)).modify("insert into Card_Account values (10000000, 10000000);");
//    }
//
//    /**
//     * Positive case testing the querying of retrieving linked accounts for a particular card from the DB
//     * @throws SQLException
//     */
//    @Test
//    public void retrieveLinkedAccountsTest() throws SQLException {
//        CardDB cardDb = Mockito.spy(new CardDB());
//        when(cardDb.getConnection()).thenReturn(connection);
//        when(rs.isBeforeFirst()).thenReturn(true);
//        doReturn(rs).when(cardDb).select("SELECT accountNum\n" +
//                "FROM Card_Account\n" +
//                "Where cardNum = '10000000'\n" +
//                "Order by accountNum");
//        when(rs.next()).thenReturn(true).thenReturn(false);
//        when(rs.getString("accountNum")).thenReturn("10000000");
//        assertSame(cardDb.retrieveLinkedAccounts("10000000").get(0), "10000000");
//    }
//
//    /**
//     * Positive case testing the querying to retrieve a Card instance with Status Ok
//     * @throws SQLException
//     */
//    @Test
//    public void retrieveOkCardTest() throws SQLException {
//        CardDB cardDb = Mockito.spy(new CardDB());
//        when(cardDb.getConnection()).thenReturn(connection);
//        when(rs.isBeforeFirst()).thenReturn(true);
//        doReturn(rs).when(cardDb).select("select cardNum, pin, issuedate, expiredate, state from Card where cardNum = '10000000'");
//        when(rs.next()).thenReturn(true);
//        when(rs.getString("pin")).thenReturn("0000");
//        when(rs.getString("issuedate")).thenReturn("01-01-2020");
//        when(rs.getString("expiredate")).thenReturn("01-01-2024");
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
//        LocalDate expectedissue = LocalDate.parse("01-01-2020", formatter);
//        LocalDate expectedexpr = LocalDate.parse("01-01-2024", formatter);
//
//        when(rs.getString("state")).thenReturn("OK");
//        assertSame(cardDb.retrieveCard("10000000").getCardNumber(), "10000000");
//        assertSame(cardDb.retrieveCard("10000000").getPin(), "0000");
//        assertTrue(0 == expectedissue.compareTo(cardDb.retrieveCard("10000000").getIssueDate()));
//        assertTrue(0 == expectedexpr.compareTo(cardDb.retrieveCard("10000000").getExpiryDate()));
//        assertSame(cardDb.retrieveCard("10000000").getStatus(), Card.Status.OK);
//    }
//
//    /**
//     * Negative case testing retrieval of a card that is blocked from the DB
//     * @throws SQLException
//     */
//    @Test
//    public void retrieveBlockedCardTest() throws SQLException {
//        CardDB cardDb = Mockito.spy(new CardDB());
//        when(cardDb.getConnection()).thenReturn(connection);
//        when(rs.isBeforeFirst()).thenReturn(true);
//        doReturn(rs).when(cardDb).select("select cardNum, pin, issuedate, expiredate, state from Card where cardNum = '10000000'");
//        when(rs.next()).thenReturn(true);
//        when(rs.getString("pin")).thenReturn("0000");
//        when(rs.getString("issuedate")).thenReturn("01-01-2020");
//        when(rs.getString("expiredate")).thenReturn("01-01-2024");
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
//        LocalDate expectedissue = LocalDate.parse("01-01-2020", formatter);
//        LocalDate expectedexpr = LocalDate.parse("01-01-2024", formatter);
//
//        when(rs.getString("state")).thenReturn("BLOCKED");
//        assertSame(cardDb.retrieveCard("10000000").getCardNumber(), "10000000");
//        assertSame(cardDb.retrieveCard("10000000").getPin(), "0000");
//        assertTrue(0 == expectedissue.compareTo(cardDb.retrieveCard("10000000").getIssueDate()));
//        assertTrue(0 == expectedexpr.compareTo(cardDb.retrieveCard("10000000").getExpiryDate()));
//        assertSame(cardDb.retrieveCard("10000000").getStatus(), Card.Status.BLOCKED);
//    }
//
//    /**
//     * Negative case testing retrieval of a Stolen card from the DB
//     * @throws SQLException
//     */
//    @Test
//    public void retrieveStolenCardTest() throws SQLException {
//        CardDB cardDb = Mockito.spy(new CardDB());
//        when(cardDb.getConnection()).thenReturn(connection);
//        when(rs.isBeforeFirst()).thenReturn(true);
//        doReturn(rs).when(cardDb).select("select cardNum, pin, issuedate, expiredate, state from Card where cardNum = '10000000'");
//        when(rs.next()).thenReturn(true);
//        when(rs.getString("pin")).thenReturn("0000");
//        when(rs.getString("issuedate")).thenReturn("01-01-2020");
//        when(rs.getString("expiredate")).thenReturn("01-01-2024");
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
//        LocalDate expectedissue = LocalDate.parse("01-01-2020", formatter);
//        LocalDate expectedexpr = LocalDate.parse("01-01-2024", formatter);
//
//        when(rs.getString("state")).thenReturn("STOLEN");
//        assertSame(cardDb.retrieveCard("10000000").getCardNumber(), "10000000");
//        assertSame(cardDb.retrieveCard("10000000").getPin(), "0000");
//        assertTrue(0 == expectedissue.compareTo(cardDb.retrieveCard("10000000").getIssueDate()));
//        assertTrue(0 == expectedexpr.compareTo(cardDb.retrieveCard("10000000").getExpiryDate()));
//        assertSame(cardDb.retrieveCard("10000000").getStatus(), Card.Status.STOLEN);}
//
//    /**
//     * Negative case testing retrieval of lost card in DB
//     * @throws SQLException
//     */
//    @Test
//    public void retrieveLostCardTest() throws SQLException {
//        CardDB cardDb = Mockito.spy(new CardDB());
//        when(cardDb.getConnection()).thenReturn(connection);
//        when(rs.isBeforeFirst()).thenReturn(true);
//        doReturn(rs).when(cardDb).select("select cardNum, pin, issuedate, expiredate, state from Card where cardNum = '10000000'");
//        when(rs.next()).thenReturn(true);
//        when(rs.getString("pin")).thenReturn("0000");
//        when(rs.getString("issuedate")).thenReturn("01-01-2020");
//        when(rs.getString("expiredate")).thenReturn("01-01-2024");
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
//        LocalDate expectedissue = LocalDate.parse("01-01-2020", formatter);
//        LocalDate expectedexpr = LocalDate.parse("01-01-2024", formatter);
//        when(rs.getString("state")).thenReturn("LOST");
//        assertSame(cardDb.retrieveCard("10000000").getCardNumber(), "10000000");
//        assertSame(cardDb.retrieveCard("10000000").getPin(), "0000");
//        assertTrue(0 == expectedissue.compareTo(cardDb.retrieveCard("10000000").getIssueDate()));
//        assertTrue(0 == expectedexpr.compareTo(cardDb.retrieveCard("10000000").getExpiryDate()));
//        assertSame(cardDb.retrieveCard("10000000").getStatus(), Card.Status.LOST);
//    }
//
//    /**
//     * Negative case testing retrieval of a card with Null status
//     * @throws SQLException
//     */
//    @Test
//    public void retrieveNullCardTest() throws SQLException {
//        CardDB cardDb = Mockito.spy(new CardDB());
//        when(cardDb.getConnection()).thenReturn(connection);
//        when(rs.isBeforeFirst()).thenReturn(true);
//        doReturn(rs).when(cardDb).select("select cardNum, pin, issuedate, expiredate, state from Card where cardNum = '10000000'");
//        when(rs.next()).thenReturn(true);
//        when(rs.getString("pin")).thenReturn("0000");
//        when(rs.getString("issuedate")).thenReturn("01-01-2020");
//        when(rs.getString("expiredate")).thenReturn("01-01-2024");
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
//        LocalDate expectedissue = LocalDate.parse("01-01-2020", formatter);
//        LocalDate expectedexpr = LocalDate.parse("01-01-2024", formatter);
//        when(rs.getString("state")).thenReturn("");
//        assertSame(cardDb.retrieveCard("10000000").getCardNumber(), "10000000");
//        assertSame(cardDb.retrieveCard("10000000").getPin(), "0000");
//        assertTrue(0 == expectedissue.compareTo(cardDb.retrieveCard("10000000").getIssueDate()));
//        assertTrue(0 == expectedexpr.compareTo(cardDb.retrieveCard("10000000").getExpiryDate()));
//        assertNull(cardDb.retrieveCard("10000000").getStatus());
//
//    }
//
//    /**
//     * Positive case testing the update query of a card
//     * @throws SQLException
//     */
//    @Test
//    public void updateAccountCardStreamTest() throws SQLException {
//        CardDB cardDb = Mockito.spy(new CardDB());
//        when(cardDb.getConnection()).thenReturn(connection);
//        cardDb.updateAccount("10000000", "0000", "08/21", "08/24", "OK");
//        verify(cardDb, times(1)).modify("UPDATE Card\n" +
//                "SET Pin = '0000', issuedate = '08/21', expiredate = '08/24', state = 'OK'\n" +
//                "WHERE cardNum = '10000000'");
//    }
//
//    /**
//     * Positive case testing the insertion of a transaction into the Transaction table in the DB
//     * @throws SQLException
//     */
//    @Test
//    public void insertTransactionTest() throws SQLException {
//        TransactionDB transactionDB = Mockito.spy(new TransactionDB());
//        when(transactionDB.getConnection()).thenReturn(connection);
//
//        UserAccount userAcc = mock(UserAccount.class);
//        ATMAccount atmAcc = mock(ATMAccount.class);
//        Deposit transaction = mock(Deposit.class);
////        Transaction transaction = new Deposit(userAcc, atmAcc, 3.6f);
//        when(transaction.getUserAccount()).thenReturn(userAcc);
//        when(userAcc.getId()).thenReturn("10000000");
//        when(transaction.getQuantity()).thenReturn(100.0);
//        when(transaction.getTransactionId()).thenReturn("10000000");
//        when(transaction.getTypeStr()).thenReturn("Deposit");
//        when(transactionDB.getTime()).thenReturn("12:00");
//        transactionDB.insertTransaction(transaction);
//        verify(transactionDB, times(1)).modify("insert into transaction values ('10000000', '100.0','10000000', 'Deposit', '12:00');");
//    }
//
//    /**
//     * Positive case testing the select query for the DB
//     * @throws SQLException
//     */
//    @Test
//    public void selectTest() throws SQLException {
//        Database adb = Mockito.spy(new AccountDB());
//        when(adb.getConnection()).thenReturn(connection);
//        when(statement.executeQuery("select * from account")).thenReturn(rs);
//        when(rs.isBeforeFirst()).thenReturn(true);
//        assertSame(adb.select("select * from account"), rs);
//
//        Database cdb = Mockito.spy(new CardDB());
//        when(cdb.getConnection()).thenReturn(connection);
//        when(statement.executeQuery("select * from account")).thenReturn(rs);
//        when(rs.isBeforeFirst()).thenReturn(true);
//        assertSame(cdb.select("select * from account"), rs);
//
//        Database tdb = Mockito.spy(new TransactionDB());
//        when(tdb.getConnection()).thenReturn(connection);
//        when(statement.executeQuery("select * from account")).thenReturn(rs);
//        when(rs.isBeforeFirst()).thenReturn(true);
//        assertSame(tdb.select("select * from account"), rs);
//    }
//
//    /**
//     * Positive case testing the formatting of Local Dates throughout the application
//     */
////    @Test
////    public void getLocalDateTest(){
////        Database db = Mockito.spy(new AccountDB());
////        assertNull(db.getLocalDate(null));
////        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
////        LocalDate ld = LocalDate.parse("01-01-2021", formatter);
////        assertTrue(0 == ld.compareTo(db.getLocalDate("01-01-2021")));
////    }
//}