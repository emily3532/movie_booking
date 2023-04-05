package ATM.utils;

public abstract class Validator<T> {


    /**
     * Stores if the entered amount is a valid amount
     */
    private final boolean valid;

    /**
     * Data of type T that is to be validated.
     */
    private final T data;

    /**
     * Reason for invalidation. Can be set using setReason() by implementing class and retrieved using
     * getReason(). Useful to display a custom error message to inform the user why the input was invalid.
     */
    private String reason;

    /**
     * Validator constructor.
     *
     * The constructor will determine if the input data is valid by calling the abstract function validateData()
     * which must be implemented by inheriting classes. Initially sets reason string to null.
     *
     * @param data T The data to be validated.
     */
    protected Validator(T data) {
        this.reason = null;
        this.data = data;
        this.valid = validateData(this.data);

    }

    /**
     * Returns true if the amount entered was valid.
     *
     * @return boolean
     */
    public boolean isValid() {
        return valid;
    }

    /**
     * Returns input data.
     * @return T
     */
    public T getData() {
        return data;
    }

    /**
     * Returns true if reason is not null (ie. if setReason() has been called with a valid string).
     * @return boolean
     */
    public boolean hasReason() {
        return this.reason != null;
    }

    /**
     * Returns the reason.
     *
     * Reason can be null so hasReason() should be used to check.
     * @return String
     */
    public String getReason() {
        return this.reason;
    }

    /**
     * Abstract function implemented by deriving classes to indicate if the input data (of type T) meets the
     * desired criteria. Function will be called in the base classes (this class) constructor and the return value
     * will set the "valid" member variable.
     *
     * It is appropriate in this function to use the setReason() function to indicate why the input was invalid
     * so error messages can be displayed to the user.
     * @param data T
     * @return boolean
     */
    protected abstract boolean validateData(T data);

    /**
     * Sets the reason string.
     *
     * Will only be set if the input String is not empty.
     * @param reason String
     */
    protected void setReason(String reason) {
        if (!reason.isEmpty()) {
            this.reason = reason;
        }
    }


}
