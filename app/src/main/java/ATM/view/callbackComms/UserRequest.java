package ATM.view.callbackComms;

/**
 * Defines some user request that can be sent as part of a transaction event (or other) to inform the Controller
 * what the users input was.
 *
 * A user's input can be set to invalid so indicate that the composed data should not be accessed.
 */
public class UserRequest<T> extends Comms<T> {

    /**
     * Constructs a UserRequest with no input. Can be used to indicate a boolean option (yes/no etc), but the
     * receiving callback must know what these options were as that information is not kept in the Request. This is
     * why it is best to wrap a UserRequest as part of a TransactionEvent to give context to the current state of
     * the system.
     * @param isValid boolean
     */
    public UserRequest(boolean isValid) {
        this(null, isValid);
    }

    /**
     * Constructs a UserRequest with some given input string.
     * @param request T
     * @param isValid boolean
     */
    public UserRequest(T request, boolean isValid) {
        super(request, isValid);
    }

}
