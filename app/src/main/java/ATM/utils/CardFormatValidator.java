package ATM.utils;

import ATM.Entities.Accounts.Account;

/**
 * Validator to check that a String matches the format of a card number. Does not make a call to the database to see
 * if the cardnumber exsits but checks the input string format.
 *
 * Checks if the number entered is 5 digits long.
 */
public class CardFormatValidator extends Validator<String> {

    public static final int MAX_NUMBER_OF_DIGITS = 5;
    public static final String MAX_NUMBER_OF_DIGITS_STRING = String.valueOf(MAX_NUMBER_OF_DIGITS);
    public static final String WRONG_DIGIT_NUMBER_ERROR_MESSAGE =
            "Card number must be " + MAX_NUMBER_OF_DIGITS_STRING + " digits long";

    /**
     * Creates a new CardFormatValidator object.
     * @param number String. The card number to validate.
     */
    public CardFormatValidator(String number) {
        super(number);
    }

    /**
     * Checks if the input string is valid. If the input is invalid the reason for error will be set and can be
     * retrieved using the getReason method.
     * @param data String
     * @return boolean
     */
    @Override
    protected boolean validateData(String data) {
        if(data == null) {
            setReason(WRONG_DIGIT_NUMBER_ERROR_MESSAGE);
            return false;
        }
        try {
            //the exception
            if (data.equals(Account.ATM_ACCOUNT_CARDNUM)) {
                return true;
            }
            Integer.parseInt(data);
            //must be 5 characters
            if(data.length() != MAX_NUMBER_OF_DIGITS) {
                setReason(WRONG_DIGIT_NUMBER_ERROR_MESSAGE);
                return false;
            }
            return true;
        }
        catch (NumberFormatException e) {
            setReason("Card input was not a number");
            return false;
        }
    }
}
