package ATM.view.callbackComms;

import javafx.util.Pair;

/**
 * A UserRequest object that contains a pair of strings as input to the event callback.
 */
public class UserPairRequest extends UserRequest<Pair<String, String>> {
    public UserPairRequest(String key, String value, boolean isValid) {
        super(new Pair<String, String>(key, value), isValid);
    }
}
