package ATM.view.events;

import java.util.Objects;

/**
 * Abstract class to define an Event. An event can be triggered by any class inheriting from AtmSceneControllerBase
 * to trigger a callback in the main ATM window interface. Some events have default behaviour depending on their derived
 * type and their data contents but all event actions (what happens when an event is triggered) can be overwritten
 * by registering a new EventHandler on a specific event and datatype. Events are matched based on derived type AND
 * contents T.
 * @param <T>
 */
public abstract class Event<T> {

    /**
     * Some data created as part of the event. Any EventHandler should know how to deal with the given
     * data type in each event.
     */
    protected T data;

    /**
     * Creates a new Event
     *
     * @param data
     */
    protected Event(T data) {
        this.data = data;
    }

    /**
     * Gets the data of the event
     * @return T
     */
    public T getData() {
        return data;
    }

    /**
     * Overwritten equals function. Checks if the derived type of both events are the same AND if the
     * the data contained in the events are the same
     * @param o Some event.
     * @return boolean
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Event)) return false;
        Event<?> event = (Event<?>) o;

        String className = event.getClass().getSimpleName();
        return (className.equals(getClass().getSimpleName()) &&
            this.data == event.getData());
    }

    @Override
    public String toString() {
        String className = this.getClass().getSimpleName();
        return className + ": " + data;
    }

    @Override
    public int hashCode() {
        return Objects.hash(data);
    }
}
