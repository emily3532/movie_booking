package ATM.view.events;

import ATM.view.callbackComms.SystemResponse;
import ATM.view.callbackComms.UserRequest;

/**
 * Defines a simple interface to facilitate callbacks from observer parties.
 */
public interface EventManager {

    /**
     * Callback function that is triggered when an event emitted matches a registered callback. The implementing class
     * will receive all events from any EventProducer and will trigger the appropriate EventHandler callback
     * which is attached to a given event. The registerCallback function will attach an EventHandler object
     * to a given event.
     * @param event Event triggering the callback
     * @param request UserRequest
     * @return SystemResponse signifying if the callback action executed successfully and any data that
     * is to be parsed back to the EventProducer
     */
    SystemResponse<?> eventCallback(Event<?> event, UserRequest<?> request) throws EventException;

    /**
     * Registers a new EventHandler to trigger on a specific Event.
     *
     * @param event Event
     * @param eventHandler EventHandler
     */
    void registerCallback(Event<?> event, EventHandler eventHandler);

    void registerCallback(Event<?> event,
                          Class<? extends SystemResponse<?>> responseType,
                          EventHandler eventHandler);

    void registerCallback(Event<?> event,
                          Class<? extends SystemResponse<?>> responseType,
                          Class<? extends UserRequest<?>> requestType,
                          EventHandler eventHandler);
}
