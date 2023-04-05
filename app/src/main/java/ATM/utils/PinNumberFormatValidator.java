package ATM.utils;

/**
 * Validator to check that a String matches the format of a pin. Does not make a call to the database to see
 * if the pin but checks the input string format.
 *
 * Checks if the number entered is 4 digits long.
 */
public class PinNumberFormatValidator extends Validator<String> {

    public static final int MAX_NUMBER_OF_DIGITS = 4;
    public static final String MAX_NUMBER_OF_DIGITS_STRING = String.valueOf(MAX_NUMBER_OF_DIGITS);
    public static final String WRONG_DIGIT_NUMBER_ERROR_MESSAGE =
            "PIN must be " + MAX_NUMBER_OF_DIGITS_STRING + " digits long";

    /**
     * Creates a new PinNumberFormatValidator object.
     * @param number String. The pin to validate.
     */
    public PinNumberFormatValidator(String number) {
        super(number);
    }

    @Override
    protected boolean validateData(String data) {
        try {
            Integer.parseInt(data);
            //must be 4 characters
            if(data.length() != MAX_NUMBER_OF_DIGITS) {
                setReason(WRONG_DIGIT_NUMBER_ERROR_MESSAGE);
                return false;
            }
            return true;
        }
        catch (NumberFormatException e) {
            setReason("Pin was not a number"); //should never get here
            return false;
        }
    }
}
