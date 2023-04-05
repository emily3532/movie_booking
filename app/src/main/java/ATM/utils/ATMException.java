package ATM.utils;

/**
 * Custom exception class. Commonly used to immediately throw an error when user input is correct or some functionality
 * fails allowing the GUI to catch the exception and the reason for failure and display this as error message
 * directly to the screen.
 */
public class ATMException extends Exception{
    /**
     * Creates a new ATMException.
     * @param reason String
     */
    public ATMException(String reason) {
        super(reason);
    }
}
