package ATM.view.callbackComms;

/**
 * A UserRequest object that contains a float as input to the event callback.
 */
public class UserAmountRequest extends UserRequest<Float> {
    public UserAmountRequest(float amount) {
        super(amount, true);
    }
}
