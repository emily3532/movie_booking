package ATM.utils;

public class MoneyWithdrawValidator extends Validator<String> {

    /**
     * Monetary value stored as a string
     */
    private float amount;

    /**
     * Creates a new MoneyWithdrawValidator object to check if the amount entered is a valid amount.
     * @param amount String
     */
    public MoneyWithdrawValidator(String amount) {
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
            double d = Double.parseDouble(amount);
//            System.out.println("Whtdoraw "+ d);
            System.out.println("Whtdoraw "+ d % 0.05);
            boolean result =  ((d*100) % 5 == 0);
            if(!result) {
                setReason("Input must be made of Australian Money values");
            }
            return result;
        }
        catch (NumberFormatException e) {
            setReason("Input was not a number");
            return false;
        }
    }

    private void setAmount(String amount) {
        if(isValid()) {
            this.amount = Float.parseFloat(amount);
        }
    }

}
