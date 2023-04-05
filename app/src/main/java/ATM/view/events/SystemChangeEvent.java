package ATM.view.events;

/**
 * SystemChangeEvent is used to indicate that a system state change should occur. Primarily this is used
 * to exit the program as well as raise Error messages/warnings.
 *
 * There is no default behaviour for a SystemChangeEvent so if a EventProducer emits this event without
 * a registered EventHandler the program will raise an error.
 */
public class SystemChangeEvent extends Event<SystemChange> {

    /**
     * Reason for system change. Optional argument to the constructor
     */
    private final String reason;

    /**
     * Creates a system change event without a reason (set to null). Use hasReason() to check if the reason exists.
     * @param system SystemChange
     */
    public SystemChangeEvent(SystemChange system) {
        super(system);
        this.reason = null;
    }

    /**
     * Creates a system change event with a given reason.
     * @param system SystemChange
     * @param reason String The reason for the system change.
     */
    public SystemChangeEvent(SystemChange system, String reason) {
        super(system);
        this.reason = reason;
    }

    /**
     * Returns the reason for the system change. Can be null. Users should check if the reason exists
     * with hasReason()
     * .
     * @return String
     */
    public String getReason() {
        return this.reason;
    }

    /**
     * Checks if the Event has a provided reason.
     * @return boolean
     */
    public boolean hasReason() {
        return reason != null;
    }

}
