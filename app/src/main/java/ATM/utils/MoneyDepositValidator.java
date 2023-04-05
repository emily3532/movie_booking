package ATM.utils;

/**
 * Used to validate an input monetary value from the user. Only money values which can be made up on
 * $5 notes will be considered valid inputs. This is used when depositing only (TODO: I think)
 */
public class MoneyDepositValidator extends Validator<String> {

    /**
     * Monetary value stored as a string
     */
    private float amount;

    /**
     * Creates a new MoneyValidator object to check if the amount entered is a valid amount.
     * @param amount String
     */
    public MoneyDepositValidator(String amount) {
        super(amount);
        setAmount(amount);
    }

    /**
     * Returns the amount as a double.
     * @return Double
     * @throws NumberFormatException Is thrown if the amount entered was invalid
     */
    public float getAmount() throws NumberFormatException {
        if(!isValid()) {
            throw new NumberFormatException();
        }
        else {
            return amount;
        }
    }

    /**
     * Checks if the entered amount is greater than 0 and can be made up of $5 notes.
     * @param amount String
     * @return boolean
     */
    @Override
    protected boolean validateData(String amount) {
        if (amount == null || amount.isEmpty()) {
            return false;
        }
        try {
            float d = Float.parseFloat(amount);
            boolean result =  (d % 5 == 0 && d >= 0);
            if(!result) {
                setReason("Input must be made of AUS$ notes only.");
            }
            return result;
        }
        catch (NumberFormatException e) {
            setReason("Input was not a number.");
            return false;
        }
    }

    private void setAmount(String amount) {
        if(isValid()) {
            this.amount = Float.parseFloat(amount);
        }
    }

}
