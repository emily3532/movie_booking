package ATM.view.controllers;

import ATM.view.callbackComms.SystemResponse;
import ATM.view.callbackComms.UserRequest;
import ATM.view.events.EventManager;
import ATM.view.events.Event;
import ATM.view.events.EventProducer;
import ATM.view.events.EventException;
import javafx.fxml.FXMLLoader;
import javafx.util.Duration;

/**
 * Abstract class that all Window (FXML) controllers should extend. Provides implementation of EventProducer
 * interface to facilitate callbacks to the EventManager to control windows, request transactions and shutdown the
 * application.
 */
public abstract class AtmSceneControllerBase implements EventProducer {

    /**
     * Event Manager interface. Facilitates callbacks upon generated events from any classes deriving from
     * AtmSceneControllerBase.
     */
    private EventManager callbackInterface;

    /**
     * The FXMLLoader used for this controller.
     */
    private FXMLLoader loader;

    /**
     * Protected constructor. Sets the callback interface and FXMLLoader to null.
     */
    protected AtmSceneControllerBase() {
        this.callbackInterface = null;
        this.loader = null;
    }

    /**
     * Sets the EventManager to this Controller. Must be called before the controller can emit Events to the Manager.
     * @param callbackInterface EventManager
     * @return boolean
     */
    public boolean attachCallbackInterface(EventManager callbackInterface) {
        if (this.callbackInterface == null) {
            this.callbackInterface = callbackInterface;
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * Sets the loader for this class. Must be called before class is used. Returns true if loader is set
     * or false if the loader was already set.
     * @param loader FXMLLoader
     * @return boolean
     */
    public boolean setLoader(FXMLLoader loader) {
        if (this.loader == null) {
            this.loader = loader;
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * Returns the loader for this interface. May be null
     * @return FXMLLoader
     */
    public FXMLLoader getLoader() {
        return loader;
    }

    /**
     * Overwritten EventProducer function allowing all controllers to emit a signal back to the EventManger.
     *
     * The callbackInterface must be set before events can be emitted. Returns the result of the callback function,
     * indicating if the event was processed successfully.
     * @param event Event T with some data T
     * @return boolean
     */
    @Override
    public SystemResponse<?> emit(Event<?> event) {
        if(this.callbackInterface == null) {
            System.out.println("Callback interface not attached.");
            return null;
        }
        else {
            try {
                return this.callbackInterface.eventCallback(event, null);
            }
            catch (EventException e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    /**
     * Emits an event from the controller to cause a callback function in the EventManager to be triggered. Emit
     * contains an Event which is used as the key to trigger the specific function on the Manager side and a
     * UserRequest (may be null) indicating the specific information to be parsed to the event callback. The callback
     * will return a SystemResponse (may be null) indicating the status (or other) if the callback.
     *
     * Callback functions must be registered with the EventManager to trigger on a specific event and the request
     * (derived from UserRequest) and response (derived from SystemResponse) must be specified. If the request
     * or response type differ from those of the types used when registering the callback function, a runtime exception
     * will be raised.
     * @param event Event.
     * @param userRequest UserRequest
     * @return SystemResponse
     */
    @Override
    public SystemResponse<?> emit(Event<?> event, UserRequest<?> userRequest) {
        if(this.callbackInterface == null) {
            System.out.println("Callback interface not attached.");
            return null;
        }
        else {
            try {
                return this.callbackInterface.eventCallback(event, userRequest);
            }
            catch (EventException e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    /**
     * Callback that is triggered by the EventManagers's Timeline object every X milliseconds. Only the currently
     * active window will have this function called.
     *
     * Useful for any timing related animations or time specific tasks.
     * @param interval The rate at which the Timeline will create a new keyframe.
     */
    public abstract void onTickCallback(Duration interval);

    /**
     * Called immediately before the window is closed. Should be used to reset any variables values accumulated
     * during the time the window was open as the Controller objects are persistent throughout the programs' lifetime
     * are the same instance for each re-opening of the window.
     *
     * Has no default behaviour but is not abstract so that only classes that need this behaviour can overwrite.
     */
    public void onClose() {}

    /**
     * Called immediately before the window is opened.
     *
     * Has no default behaviour but is not abstract so that only classes that need this behaviour can overwrite.
     */
    public void onOpen() {}

}
