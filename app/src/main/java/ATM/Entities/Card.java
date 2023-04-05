package ATM.Entities;
import ATM.Database.CardDB;
import ATM.utils.ATMException;
import ATM.utils.CardFormatValidator;
import org.checkerframework.checker.units.qual.C;

import javax.annotation.Nonnull;
import java.sql.SQLException;
import java.time.Clock;
import java.time.LocalDate;
import java.util.List;

/**
 * Object that gets passed around to encode the information required for
 * cards
 */
public class Card {

    public enum Status {
        BLOCKED,
        STOLEN,
        LOST,
        OK
    }

    public final String cardNumber;
    private final String pin;
    private Status status;
    private LocalDate issueDate;
    private LocalDate expiryDate;
    private int attempts;
    private String reason;
    private static CardDB db = new CardDB();
    private static Clock clock = Clock.systemDefaultZone();

    public Card(String cardNumber, String pin, Status status, LocalDate issueDate, LocalDate expiryDate) {
        this.cardNumber = cardNumber;
        this.pin = pin;
        this.issueDate = issueDate;
        this.expiryDate = expiryDate;
        this.attempts = 0;
        this.status = status;
        this.reason = null;

    }
    public static void setDB(CardDB db) {
        Card.db = db;
    }

    public static void setClock(Clock c) {
        Card.clock = c;
    }

    public String getCardNumber() {
        return this.cardNumber;
    }

    public String getPin() {
        return this.pin;
    }

    public Status getStatus() {
        return this.status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public LocalDate getIssueDate() {
        return this.issueDate;
    }

    public void setIssueDate(LocalDate issueDate) {
        this.issueDate = issueDate;
    }

    public LocalDate getExpiryDate() {
        return this.expiryDate;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }

    public int getAttempts() {
        return this.attempts;
    }

    public void setAttempts(int attempts) {
        this.attempts = attempts;
    }

    public CardDB getDb() {
        return db;
    }

    public boolean hasReason() {return this.reason != null;}

    public String getReason() {return this.reason;}

    /**
     * Verify the input pin is correct. Will block card if
     * more than three calls are made.
     * @param inputPin the pin input by user
     * @return true if successful the pins match and unblocked, false otherwise
     */
    public boolean checkPin(String inputPin) {
        if (attempts >= 3) {
            this.status = Card.Status.BLOCKED;
            return false;
        }
        // can't authenticate blocked,lost or stolen cards
        else if (status != Card.Status.OK) {
            attempts +=1;
            return false;
        }

        if (inputPin == null || inputPin.length() != 4) {
            attempts += 1;
            return false;
        }
        // can only have three attempts (0st, 1nd and 2rd)
        if (attempts < 3) {
            // check for match and reset attempts if it matches
            if (getPin().equals(inputPin)) {
                attempts = 0;
                return true;
            }
        } 

        attempts += 1;
        return false;
    }





    /**
     * Checks that the card is between its issue and expiry dates
     * and has not been reported stolen or lost.
     *
     * @return Whether the card is valid or not
     */
    public boolean isValid() {
        System.out.println(status);
        if (status != Status.OK) {
            setReason("We are tremendously but this card appears to be " + status);
            return false;
        }

        // TODO: replace LocalDate.now() with LocalDate.now(clock) so we can mock the times
        LocalDate current = LocalDate.now(Card.clock);
        if (current.isBefore(issueDate)) {
            setReason("Card is not active as the current data is before its issue data.");
            return false;
        }

        if (current.isAfter(expiryDate)) {
            setReason("Card use is prohibited after expiry date.");
            return false;
        }


        return true;
    }

//    public static boolean validateCardNum(String cardnum) {
//        CardFormatValidator validator = new CardFormatValidator(cardnum);
//        return validator.isValid();
////        if (cardnum == null) {
////            return false;
////        }
////
////        // card number must be 5 digits
////        if (cardnum.length() != 5) {
////            return false;
////        }
////
////        // must be all digits
////        //Jesse note: can just try converting to Integer and if that fails its not a number
////        for (int i = 0; i < cardnum.length(); i++) {
////            if (!Character.isDigit(cardnum.charAt(i))) {
////                return false;
////            }
////        }
//
//    }


    /**
     * Query the database to get the card info and return a populated Card
     * object with this info. Also validates the card and sets the status
     * @param cardNumber The card's number (5 digits)
     * @return A populated Card object
     */
    @Nonnull
    public static Card getCardInfo(String cardNumber) throws ATMException {
        if (cardNumber == null) {
            throw new ATMException("Card Number is NULL somehow...?");
        }

        CardFormatValidator validator = new CardFormatValidator(cardNumber);
        if (!validator.isValid()) {
            throw new ATMException(validator.getReason());
        }

        try {
            return db.retrieveCard(cardNumber);
        } catch (SQLException e) {
            throw new ATMException(e.getMessage());
//            db.printSQLException(e);
        }

//        return null;
    }

    /**
     * Retrieves all associated account numbers to a card
     * @param cardnum The card's number
     * @return A list of strings of account numbers
     */
    public static List<String> retrieveLinkedAccountNumbers(String cardnum) throws ATMException {
        if (cardnum == null) {
            throw new ATMException("How did you enter NULL?");
        }

        CardFormatValidator validator = new CardFormatValidator(cardnum);
        if (!validator.isValid()) {
            throw new ATMException(validator.getReason());
        }

        try {
            return db.retrieveLinkedAccounts(cardnum);
        } catch (SQLException e) {
            throw new ATMException(e.getMessage());
        }

//        return null;
    }

    /**
     * Sets reason variable in the case of the card being invalid. Results in hasReason() returning true.
     * @param reason String
     */
    public void setReason(String reason) {
        if (!reason.isEmpty()) {
            this.reason = reason;
        }
    }

}
