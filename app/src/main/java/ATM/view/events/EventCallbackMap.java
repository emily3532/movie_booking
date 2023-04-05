package ATM.view.events;

import ATM.view.callbackComms.BooleanResponse;
import ATM.view.callbackComms.SystemResponse;
import ATM.view.callbackComms.UserRequest;
import ATM.view.windows.WindowType;
import ATM.view.windows.Windows;
import ATM.view.windows.WindowsManager;

import java.util.HashMap;

/**
 * Managers all callbacks from EventProducers. Realises the EventManager interface to facilitate
 * classes to register callbacks on specific events and tell the callback the UserRequest (if any) and SystemResponse
 * (if any) that should be received. This allows the input output type to be properly cast at either end of the callback.
 */
public class EventCallbackMap  implements EventManager {


    /**
     * Wrapper class to hold the data for a specific event. Contains the event type, the EventHandler (which contains
     * the actual callback), and the UserRequest type (if any != null) and the SystemResponse type (if any != null)
     * for this callback. The expected types are used to check that the actual input/output types are expected
     * and a runtime Exception will occur if they do not match (i'm sure there is a way to make this fail at compile
     * time, but my Java isn't good enough for this currently!)
     */
    private class CallbackBlock {

        /**
         * The Event that triggers this callback. Events are matches based on derived typename and capture type.
         */
        private Event<?> event;
        /**
         * EventHandler that contains the callback function. Implemented as during the function registration.
         */
        private EventHandler handler;
        /**
         * Expected input type. If null, the callback expects there to be no input type and the overloaded
         * registerCallback without this type can be used.
         */
        private Class<? extends UserRequest<?>> useRequestTypeObject;

        /**
         * Expected response type. If null, the callback expects there to be no input type and the overloaded
         * registerCallback without this type can be used.
         */
        private Class<? extends SystemResponse<?>> systemResponseTypeObject;

        /**
         * Creates a callback block. UserResponseType and SystemResponseType will be set to null indicating
         * that the callback has a void input and void (actually null) return type.
         * @param event Event
         * @param handler EventHandler
         */
        public CallbackBlock(Event<?> event, EventHandler handler) {

            this(event, handler, null, null);
        }

        /**
         * Creates a callback block. SystemResponse type will be set to null indicating that the callback has a null
         * return type.
         * @param event Event
         * @param handler EventHandler
         * @param useRequestTypeObject Class that extends UserRequest
         */
        public CallbackBlock(Event<?> event, EventHandler handler,
                             Class<? extends UserRequest<?>> useRequestTypeObject) {

            this(event, handler, useRequestTypeObject, null);
        }

        /**
         * Creates a callback block
         * @param event Event
         * @param handler EventHandler
         * @param useRequestTypeObject Class that extends UserRequest
         * @param systemResponseTypeObject Class that extends SystemResponse
         */
        public CallbackBlock(Event<?> event, EventHandler handler,
                             Class<? extends UserRequest<?>> useRequestTypeObject,
                             Class<? extends SystemResponse<?>> systemResponseTypeObject) {
            this.event = event;
            this.handler = handler;
            this.useRequestTypeObject = useRequestTypeObject;
            this.systemResponseTypeObject = systemResponseTypeObject;

        }

        public Class<? extends SystemResponse<?>> getSystemResponseTypeObject() {
            return systemResponseTypeObject;
        }

        public Class<? extends UserRequest<?>> getUseRequestTypeObject() {
            return useRequestTypeObject;
        }

        public EventHandler getHandler() {
            return handler;
        }
    }

    private final HashMap<Event<?>, CallbackBlock> callbackBlockMap;
    private final WindowsManager windowsManager;

    /**
     * Creates an instance of an EventCallbackMap.
     * @param windowsManager WindowsManager
     */
    public EventCallbackMap(WindowsManager windowsManager) {
        this.windowsManager = windowsManager;
        this.callbackBlockMap = new HashMap<>();
    }

    /**
     * Primary eventCallback triggered (synchronously) by all EventProducers. Once called, the associated
     * CallbackBlock will be found (if there is one) and the EventHandler function will be called.
     *
     * The UserRequest type and SystemResponse type are check to ensure they match with the types provided
     * when the EventHandler was registered - if they do not, an EventException is thrown.
     *
     * Some EventTypes have default behaviour. If they are called without an Event registered for them, the default
     * behaviour will be used.
     *
     * @param event Event triggering the callback
     * @param request UserRequest.
     * @return SystemResponse
     * @throws EventException is thrown when the SystemResponse/USerRequest type does not match the types
     * provided when the EventHandler was registered.
     */
    @Override
    public SystemResponse<?> eventCallback(Event<?> event, UserRequest<?> request) throws EventException{
        for(Event<?> registeredEvents : this.callbackBlockMap.keySet()) {
            if(registeredEvents.equals(event)) {
                CallbackBlock callbackBlock = this.callbackBlockMap.get(registeredEvents);

                if (!checkUserRequestType(callbackBlock, request)) {
                    throw new EventException("Request Type mismatch. Expected type " +
                            callbackBlock.getUseRequestTypeObject() + " and got type" + request);
                }
                EventHandler eventHandler = callbackBlock.getHandler();
                SystemResponse<?> response =  eventHandler.handle(event, request);

                if (!checkSystemResponseType(callbackBlock, response)) {
                    throw new EventException("Response Type mismatch. Expected type " +
                            callbackBlock.getSystemResponseTypeObject() + " and got type" + response);
                }
                return response;

            }
        }

        //default behaviour
        if (event instanceof SceneChangeEvent) {
            WindowType windowType = this.windowsManager.getWindow((Windows)event.getData());
            this.windowsManager.changeWindow(windowType);
            return new BooleanResponse(true);
        }
        System.out.println("Event " + event + " does not have an associated callback");
        return new BooleanResponse(false);
    }

    /**
     * Registered an EventHandler to trigger on a specific event type. Expects the UserRequest type to be
     * null and the SystemResponse type to also be null.
     * @param event Event
     * @param eventHandler EventHandler
     */
    @Override
    public void registerCallback(Event<?> event, EventHandler eventHandler) {
        this.callbackBlockMap.put(event, new CallbackBlock(event, eventHandler,
                null, null));
    }

    /**
     * Registered an EventHandler to trigger on a specific event type. Expects only the UserRequest type to be
     * null.
     * @param event Event
     * @param responseType Class that extends SystemResponse
     * @param eventHandler EventHandler
     */
    @Override
    public void registerCallback(Event<?> event,
                                 Class<? extends SystemResponse<?>> responseType,
                                 EventHandler eventHandler) {
        this.callbackBlockMap.put(event, new CallbackBlock(event, eventHandler,
                null, responseType));
    }

    /**
     * Registered an EventHandler to trigger on a specific event type. The handler should expect a non-null UserRequest
     * from the EventProducer and the EventHandler to return a non-null SystemResponse type.
     * @param event Event
     * @param responseType Class that extends SystemResponse
     * @param requestType Class that extends UserRequest
     * @param eventHandler EventHandler
     */
    @Override
    public void registerCallback(Event<?> event,
                                 Class<? extends SystemResponse<?>> responseType,
                                 Class<? extends UserRequest<?>> requestType,
                                 EventHandler eventHandler) {
        this.callbackBlockMap.put(event, new CallbackBlock(event, eventHandler,
                requestType, responseType));

    }



    private boolean checkSystemResponseType(CallbackBlock block,
                                       SystemResponse<?> response) throws EventException {
        try {
            if (response == null && block.getSystemResponseTypeObject() == null) {
                return true;
            }
            return block.getSystemResponseTypeObject().equals(response.getClass());
        }
        catch (Exception e) {
            throw new EventException("Response Type mismatch. Expected type " +
                    block.getSystemResponseTypeObject() + " and got type" + response);
        }
    }

    private boolean checkUserRequestType(CallbackBlock block, UserRequest<?> request) throws EventException {
        try {
            if (request == null && block.getUseRequestTypeObject() == null) {
                return true;
            }
            System.out.println(request.getClass());
            return block.getUseRequestTypeObject().equals(request.getClass());
        }
        catch (Exception e) {
            throw new EventException("Request Type mismatch. Expected type " +
                    block.getUseRequestTypeObject() + " and got type" + request);
        }
    }
}
