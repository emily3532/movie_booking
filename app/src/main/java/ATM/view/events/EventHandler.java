package ATM.view.events;

import ATM.view.callbackComms.SystemResponse;
import ATM.view.callbackComms.UserRequest;

/**
 * An interface to define a callback function that will be triggered on receiving a specific event. The EventHandler
 * must know how to deal with the TYPE of Event, where type = T The manager will ensure that T is the same type as registered
 * with the EventHandler so the handler implementation must match.
 */
public interface EventHandler {

    /**
     * Function triggered when a class emits a type of event. Function must be registered with the manager
     *
     * @param event Event can be any derived type of Event.
     * @param request UserRequest
     * @return SystemResponse
     */
    SystemResponse<?> handle(Event<?> event, UserRequest<?> request);
}
