package ATM.view.callbackComms;


import java.util.Objects;

/**
 * Abstract comms class. Forms the basis of both the SystemResponse and the UserRequest type object's that are
 * used by the EventProducer and Manager classes to generalise communication between the controller windows
 * and the manager.
 * @param <T>
 */
public abstract class Comms<T> {

    /**
     * Datatype of the Comms class. Used to differentiate different responses of the same derived class (type).
     */
    private final T data;

    /**
     * If the input String was valid for whatever the request was.
     */
    private final boolean valid;

    public Comms(boolean isValid) {
        this(null, isValid);
    }


    public Comms(T data, boolean isValid) {
        this.data = data;
        this.valid = isValid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Comms)) return false;

        Comms<?> comms = (Comms<?>)o;


        //check datatypes are the same -> what about null?
        String className = comms.getClass().getSimpleName();
        return (className.equals(getClass().getSimpleName()) &&
                this.data.getClass().getSimpleName().equals(
                        comms.data.getClass().getSimpleName()));
    }

    @Override
    public int hashCode() {
        return Objects.hash(data, valid);
    }

    /**
     * Checks if the request is none
     * @return boolean
     */
    public boolean hasData() {
        return data != null;
    }

    /**
     * Returns the users input request.
     * @return String
     */
    public T getData() {
        return this.data;
    }

    /**
     * Checks if the users request was valid.
     * @return boolean
     */
    public boolean isValid() {
        return this.valid;
    }

}
