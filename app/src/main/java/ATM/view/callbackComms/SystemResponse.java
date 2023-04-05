package ATM.view.callbackComms;

/**
 * SystemResponse defines a class that will be returned by the EventManager to the Producer interface after the emit
 * function has been called. Indicates to the controller the success (or other) of their event request.
 * @param <T>
 */
public class SystemResponse<T> extends Comms<T> {

    /**
     * Constructs a SystemResponse with an indication of the status of the event function.
     * @param isValid boolean
     */
    public SystemResponse(boolean isValid) {
        this(null, isValid);
    }

    /**
     * Constructs a SystemResponse with some given input and an indication of the status of the event function.
     * @param request T
     * @param isValid boolean
     */
    public SystemResponse(T request, boolean isValid) {
        super(request, isValid);
    }
}
