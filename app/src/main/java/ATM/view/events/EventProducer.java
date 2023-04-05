package ATM.view.events;

import ATM.view.callbackComms.SystemResponse;
import ATM.view.callbackComms.UserRequest;

/**
 * EventProducer Interface. Allows communication from a class to a EventManager interface via registered callbacks
 * from a specific event. For specific behaviour to occur when an Event is emitted, a EventHandler must be registered
 * with the manager using a specific Event as the key. The Events are matched by comparing both the derived class
 * and the type of data within the event.
 */
public interface EventProducer {

    /**
     * Emits a specific event to the EventManager to trigger a callback.
     * @param event Event T with some data T
     * @return The result of the callback. Can be used to indicate if the desired behaviour was executed successfully.
     */
    SystemResponse<?> emit(Event<?> event);

    SystemResponse<?> emit(Event<?> event, UserRequest<?> userRequest);
}
