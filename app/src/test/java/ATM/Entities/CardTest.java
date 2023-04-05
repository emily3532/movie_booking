package ATM.Entities;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.sql.SQLException;
import java.time.Clock;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;

import ATM.Entities.Card;
import ATM.Entities.Accounts.UserAccount;
import ATM.utils.ATMException;
import ATM.Database.CardDB;

public class CardTest {

    /**
     * Basic constructor test. Positive Test case.
     * 
     */
    @Test
    public void testConstructor() {
        Card c = new Card("12345", "1234", Card.Status.OK, LocalDate.of(2019, 1, 1), LocalDate.of(2021, 1, 1));
        assertNotNull(c);
        assertEquals("12345", c.cardNumber);
        assertEquals(Card.Status.OK, c.getStatus());
    }

    /**
     * Test the check pin function for positive inputs (string of length four that are all digits).
     */
    @Test
    public void testCheckPinBasicPositiveCases() {
        Card c = new Card("12345", "1234", Card.Status.OK, LocalDate.of(2019, 1, 1), LocalDate.of(2021, 1, 1));
        assertTrue(c.checkPin("1234"));
        assertFalse(c.checkPin("4321"));

        c = new Card("12345", "7777", Card.Status.OK, LocalDate.of(2019, 1, 1), LocalDate.of(2021, 1, 1));
        assertTrue(c.checkPin("7777"));
        assertFalse(c.checkPin("7778"));

        c = new Card("12345", "1100", Card.Status.OK, LocalDate.of(2019, 1, 1), LocalDate.of(2021, 1, 1));
        assertTrue(c.checkPin("1100"));
        assertFalse(c.checkPin("1101"));
    }

    /**
     * Test the check pin function against edge cases
     * - Empty string
     * - Wrong string length (!= 4)
     * - Check that pins with leading 0's do not lose them
     * and match pins with the same integer value
     * - Test cards with missing pins never match
     */
    @Test
    public void testCheckPinEdgeCases() {
        // empty string passed as input pin and empty string pin for card
        Card c = new Card("12345", "", Card.Status.OK, LocalDate.of(2019, 1, 1), LocalDate.of(2021, 1, 1));
        assertFalse(c.checkPin(""));
        assertFalse(c.checkPin("4321"));

        // pin that is to short stored in card, should always be fail
        c = new Card("12345", "11", Card.Status.OK, LocalDate.of(2019, 1, 1), LocalDate.of(2021, 1, 1));
        assertFalse(c.checkPin("11"));
        assertFalse(c.checkPin("1111"));

        // check leading zeros are not lost
        c = new Card("12345", "0000", Card.Status.OK, LocalDate.of(2019, 1, 1), LocalDate.of(2021, 1, 1));
        assertTrue(c.checkPin("0000"));
        assertFalse(c.checkPin("0"));

        // null passed as input should not match a missing pin, should always fail
        c = new Card("12345", null, Card.Status.OK, LocalDate.of(2019, 1, 1), LocalDate.of(2021, 1, 1));
        assertFalse(c.checkPin(null));


    }

    /**
     * Check pin function against the negative case where the input pin is null
     */
    @Test
    public void testCheckPinBasicNegativeCases() {
        // null passed as input string
        Card c = new Card("12345", "1234", Card.Status.OK, LocalDate.of(2019, 1, 1), LocalDate.of(2021, 1, 1));
        assertTrue(c.checkPin("1234"));
        assertFalse(c.checkPin(null));

    }

    /**
     * Test Positive cases with differing card status'.
     * - Blocked card should never return true
     * - Stolen card should never return true
     * - Lost card should never return true
     */
    @Test
    public void testCheckPinBadStatusCases() {
        // card is blocked, should never be true
        Card c = new Card("12345", "1234", Card.Status.BLOCKED, LocalDate.of(2019, 1, 1), LocalDate.of(2021, 1, 1));
        assertFalse(c.checkPin("1234"));
        assertFalse(c.checkPin(null));

        // card is stolen, should never be true
        c = new Card("12345", "1234", Card.Status.STOLEN, LocalDate.of(2019, 1, 1), LocalDate.of(2021, 1, 1));
        assertFalse(c.checkPin("1234"));
        assertFalse(c.checkPin(null));

        // card is lost, should never be true
        c = new Card("12345", "1234", Card.Status.LOST, LocalDate.of(2019, 1, 1), LocalDate.of(2021, 1, 1));
        assertFalse(c.checkPin("1234"));
        assertFalse(c.checkPin(null));

    }

    /**
     * Test check pin function correctly blocks a card.
     * - After three incorrect attempts any further attempts will fail and the card should be blocked
     * 
     */
    @Test
    public void testCheckPinAttemptStateCases() {
        // card pin is attempted more than three times
        Card c = new Card("12345", "1234", Card.Status.OK, LocalDate.of(2019, 1, 1), LocalDate.of(2021, 1, 1));
        assertFalse(c.checkPin("1111"));
        assertEquals(Card.Status.OK, c.getStatus());
        assertFalse(c.checkPin("1111"));
        assertEquals(2, c.getAttempts());
        assertEquals(Card.Status.OK, c.getStatus());
        assertFalse(c.checkPin("1111"));
        assertEquals(3, c.getAttempts());
        //needed to check pin again to change status to blocked
        assertFalse(c.checkPin("1111"));
        assertNotEquals(Card.Status.OK, c.getStatus());
        assertEquals(Card.Status.BLOCKED, c.getStatus());
        assertFalse(c.checkPin("1234"));
    }

    /**
     * Test check pin function will reset the attempt count to block
     * a card after a correct entry (within three attempts).
     */
    @Test
    public void testCheckPinAttemptsResetAfterSuccess() {
        // card pin is attempted state is reset after correct pin
        Card c = new Card("12345", "1234", Card.Status.OK, LocalDate.of(2019, 1, 1), LocalDate.of(2021, 1, 1));
        assertFalse(c.checkPin("1111"));
        assertFalse(c.checkPin("1111"));
        assertEquals(2, c.getAttempts());
        assertTrue(c.checkPin("1234"));
        assertEquals(0, c.getAttempts());
        assertEquals(Card.Status.OK, c.getStatus());
        assertTrue(c.checkPin("1234"));
    }


    /**
     * Test Positive cases for each of the non OK statuses.
     * The card should never be valid.
     */
    @Test
    public void isValidInvalidStatus() {
        Card c = new Card("12345", "1234", Card.Status.LOST, LocalDate.of(2019, 1, 1), LocalDate.of(2021, 1, 1));
        assertFalse(c.isValid());
        c = new Card("12345", "1234", Card.Status.STOLEN, LocalDate.of(2019, 1, 1), LocalDate.of(2021, 1, 1));
        assertFalse(c.isValid());
        c = new Card("12345", "1234", Card.Status.BLOCKED, LocalDate.of(2019, 1, 1), LocalDate.of(2021, 1, 1));
        assertFalse(c.isValid());
    }

    /**
     * Positive case checking that a card before its issue date is not valid
     * and the reason is correctly set.
     * 
     */
    @Test
    public void isValidBeforeIssueCard() {
        Card c = Mockito.spy(new Card("12345", "1234", Card.Status.OK, LocalDate.of(2019, 1, 1), LocalDate.of(2021, 1, 1)));
        LocalDate ld = LocalDate.of(1900, 1, 1);
        Clock fixed = Clock.fixed(ld.atStartOfDay(ZoneId.systemDefault()).toInstant(), ZoneId.systemDefault());
        Card.setClock(fixed);
        assertFalse(c.isValid());
        assertEquals("Card is not active as the current data is before its issue data.", c.getReason());
    }

    /**
     * Positive case checking that a card after its expiry date is not valid
     * and the reason is correctly set.
     */
    @Test
    public void isValidExpiredCard() {
        Card c = Mockito.spy(new Card("12345", "1234", Card.Status.OK, LocalDate.of(2019, 1, 1), LocalDate.of(2021, 1, 1)));
        LocalDate ld = LocalDate.of(2021, 4, 1);
        Clock fixed = Clock.fixed(ld.atStartOfDay(ZoneId.systemDefault()).toInstant(), ZoneId.systemDefault());
        Card.setClock(fixed);
        assertFalse(c.isValid());
        assertEquals("Card use is prohibited after expiry date.", c.getReason());
    }

    /**
     * Test that a card between its expiry and issue date is marked as valid
     * 
     */
    @Test
    public void isValidCard() {
        Card c = Mockito.spy(new Card("12345", "1234", Card.Status.OK, LocalDate.of(2019, 1, 1), LocalDate.of(2021, 1, 1)));
        LocalDate ld = LocalDate.of(2020, 4, 1);
        Clock fixed = Clock.fixed(ld.atStartOfDay(ZoneId.systemDefault()).toInstant(), ZoneId.systemDefault());
        Card.setClock(fixed);
        assertTrue(c.isValid());
    }

    /**
     * Test has reason getter and stter methods
     * 
     */
    @Test
    public void testHasReason() {
        Card c = new Card("12345", "1234", Card.Status.OK, LocalDate.of(2000, 1, 1), LocalDate.of(2012, 1, 1));
        assertFalse(c.hasReason());
        c.setReason("test_reason");
        assertEquals("test_reason", c.getReason());
        assertTrue(c.hasReason());
        c.setReason("");
        assertEquals("test_reason", c.getReason());
    }

    /**
     * Negative case checking that attempting to get a null card from database throws and exception
     * with correct message.
     * 
     */
    @Test 
    public void retrieveCardNullCardNumber() {
        try {
            Card.getCardInfo(null);
            fail();
        } catch (ATMException e) {
            assertEquals("Card Number is NULL somehow...?", e.getMessage());
        }
    }

    /**
     * Edge case checking that attempting to get a card with a cardnumber of the wrong size from database throws and exception
     * with correct message.
     * 
     */
    @Test 
    public void retrieveCardWrongCardNumberLen() {
        try {
            Card.getCardInfo("1234");
            fail();
        } catch (ATMException e) {
            assertEquals("Card number must be 5 digits long", e.getMessage());
        }
    }

    /**
     * Edge case checking that attempting to get a card with card number of empty string from database throws and exception
     * with correct message.
     * 
     */
    @Test 
    public void retrieveCardEmptyString() {
        try {
            Card.getCardInfo("");
            fail();
        } catch (ATMException e) {
            assertEquals("Card input was not a number", e.getMessage());
        }
    }

    /**
     * Negative case checking that attempting to get a card where the card number has non digits from database throws and exception
     * with correct message.
     * 
     */
    @Test 
    public void retrieveCardRightLenNonDigits() {
        try {
            Card.getCardInfo("aaaaa");
            fail();
        } catch (ATMException e) {
            assertEquals("Card input was not a number", e.getMessage());
        }
    }

    /**
     * Positive case checking that attempting to get an existing card from database returns the correct card object
     * 
     */
    @Test 
    public void retrieveCardExists() throws SQLException, ATMException {
        CardDB db = mock(CardDB.class);
        Card c = new Card("12345", "1234", Card.Status.OK, LocalDate.of(2000, 1, 1), LocalDate.of(2012, 1, 1));
        Card.setDB(db);
        when(db.retrieveCard("12345")).thenReturn(c);
        assertSame(c, Card.getCardInfo("12345"));
        Card.setDB(new CardDB());
    }

    /**
     * Edge case checking that an SQLException triggered by trying to retrieve a card from the db is handled
     * 
     */
    @Test 
    public void retrieveCardSQLException() throws SQLException, ATMException {
        CardDB db = mock(CardDB.class);
        Card c = new Card("12345", "1234", Card.Status.OK, LocalDate.of(2000, 1, 1), LocalDate.of(2012, 1, 1));
        Card.setDB(db);
        when(db.retrieveCard("12345")).thenThrow(new SQLException("A message"));
        try {
            Card.getCardInfo("12345");
            Card.setDB(new CardDB());
            fail();
        } catch (ATMException e) {
            assertEquals("A message", e.getMessage());
        }
        Card.setDB(new CardDB());
    }

    /**
     * Negative case checking that the correct exception is raised when
     * a card with non digits tries to get linked accounts.
     */
    @Test 
    public void retrieveLinkedAccountsRightLenNonDigits() {
        try {
            Card.retrieveLinkedAccountNumbers("aaaaa");
            fail();
        } catch (ATMException e) {
            assertEquals("Card input was not a number", e.getMessage());
        }
    }

    /**
     * Edge case checking that the correct exception is raised when
     * a cardnumber is given as the empty string tries to get linked accounts.
     */
    @Test 
    public void retrieveLinkedAccountsEmptyString() {
        try {
            Card.retrieveLinkedAccountNumbers("");
            fail();
        } catch (ATMException e) {
            assertEquals("Card input was not a number", e.getMessage());
        }
    }

    /**
     * Negative case checking that the correct exception is raised when
     * a null cardnumber tries to get linked accounts.
     */
    public void retrieveLinkedAccountsNullCardNumber() {
        try {
            Card.retrieveLinkedAccountNumbers(null);
            fail();
        } catch (ATMException e) {
            assertEquals("Card Number is NULL somehow...?", e.getMessage());
        }
    }


    /**
     * Negative case checking that the correct exception is raised when
     * a cardnumber with the wrong length tries to get linked accounts.
     */
    @Test 
    public void retrieveLinkedAccountsWrongCardNumberLen() {
        try {
            Card.retrieveLinkedAccountNumbers("1234");
            fail();
        } catch (ATMException e) {
            assertEquals("Card number must be 5 digits long", e.getMessage());
        }
    }

    /**
     * Positive case checking that the function correctly returns a list object
     * given by the database upon a valid cardnumber being given as an argument
     */
    @Test 
    public void retrieveLinkedAccountsExists() throws SQLException, ATMException {
        CardDB db = mock(CardDB.class);
        Card.setDB(db);
        List<String> ls = new ArrayList<String>();
        when(db.retrieveLinkedAccounts("12345")).thenReturn(ls);
        assertSame(ls, Card.retrieveLinkedAccountNumbers("12345"));
        Card.setDB(new CardDB());
    }

    /**
     * Positive case checking that the function correctly catches then re throws
     * any Database Errors as ATMExceptions with the correct message.
     */
    @Test 
    public void retrieveLinkedAccountsSQLException() throws SQLException, ATMException {
        CardDB db = mock(CardDB.class);
        Card.setDB(db);
        when(db.retrieveLinkedAccounts("12345")).thenThrow(new SQLException("A message"));
        try {
            Card.retrieveLinkedAccountNumbers("12345");
            Card.setDB(new CardDB());
            fail();
        } catch (ATMException e) {
            assertEquals("A message", e.getMessage());
        }
        Card.setDB(new CardDB());
    }


    /**
     * Trivial Cases checking that the getter and setter methods work
     */
    @Test
    public void testTrivialGetterSetterMethods() {
        LocalDate expiry = LocalDate.of(2021, 1, 1);
        LocalDate issue = LocalDate.of(2019, 1, 1);
        Card c = new Card("12345", "1234", Card.Status.OK, LocalDate.of(2000, 1, 1), LocalDate.of(2012, 1, 1));
        c.setExpiryDate(expiry);
        c.setIssueDate(issue);
        assertSame(expiry, c.getExpiryDate());
        assertSame(issue, c.getIssueDate());
        c.setAttempts(90);
        assertEquals(90, c.getAttempts());
        c.setStatus(Card.Status.BLOCKED);
        assertEquals(Card.Status.BLOCKED, c.getStatus());
        assertEquals("12345", c.getCardNumber());
        CardDB testDB = new CardDB();
        Card.setDB(testDB);
        assertSame(testDB, c.getDb());
    }


}