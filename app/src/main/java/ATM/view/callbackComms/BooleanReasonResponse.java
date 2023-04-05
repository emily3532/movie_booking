package ATM.view.callbackComms;

/**
 * System response type containing a boolean flag to indicate validity and a reason string to
 * indicate why the response was invalid.
 */
public class BooleanReasonResponse extends BooleanResponse{
    private final String reason;

    /**
     * Creates a new BooleanReasonResponse class.
     * @param response boolean
     * @param reason String
     */
    public BooleanReasonResponse(boolean response, String reason) {
        super(response);
        this.reason = reason;
    }

    /**
     * Gets the reason associated with this class. Will be null if the reason was not set
     * @return String
     */
    public String getReason() {
        return reason;
    }
}
