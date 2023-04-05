package ATM.view.callbackComms;

/**
 * System response containing a boolean to indicate if the response was valid or not.
 */
public class BooleanResponse extends SystemResponse<Boolean> {

    /**
     * Creates a new BooleanResponse object.
     * @param result boolean
     */
    public BooleanResponse(Boolean result) {
        super(result);
    }
}
