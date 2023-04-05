package ATM.view.events;


import ATM.Entities.Transactions.Transaction;
import ATM.view.callbackComms.UserRequest;


/**
 * TransactionEvent is used to indicate that a transaction should take place. The transaction types are defined
 * within the Transaction interface. This class provides functionality to include what the users input what so
 * that the EventManager can construct the desired transaction appropriately. Often with a transaction there
 * will be a desired change of scene but the user should implement this in the callback function or request one directly
 * if the transaction was successful.
 *
 * There is no default behaviour for a TransactionEvent so if a EventProducer emits this event without
 *  * a registered EventHandler the program will raise an error.
 */
public class TransactionEvent extends Event<Transaction.TransactionType> {


    /**
     * Makes a new TransactionEvent with a given TransactionType without some user input that is relevant
     * for thr transaction.
     *
     * @param data TransactionType
     */
    public TransactionEvent(Transaction.TransactionType data) {
        super(data);
    }
}