package ATM.Entities.Transactions;

import ATM.Entities.Accounts.ATMAccount;
import ATM.Entities.Accounts.Account;
import ATM.Entities.Accounts.UserAccount;

import java.sql.SQLException;
import java.util.Date;
import java.util.Objects;

import ATM.Database.TransactionDB;
import ATM.utils.ATMException;

/**
 * Object that gets passed around to encode the information required for
 * operations
 */
public abstract class Transaction {
    // TODO (jesse): why are these public? at least protected?
    protected ATMAccount atmAccount = null;
    protected UserAccount userAccount = null;
    protected TransactionResult result;
    protected TransactionType type;
    protected double quantity;
    protected String transactionId;
    protected final String typeStr;
    protected static TransactionDB db = new TransactionDB();

    /**
     * Records the type of transaction (balance/deposit/withdrawal)
     */
    public enum TransactionType {
        BALANCE, DEPOSIT, WITHDRAWAl
    }

    /**
     * Records the result status of a transaction
     */
    public enum TransactionResult {
        INIT, SUCCESS, FAIL, ERR,
    }

    /**
     * Initializes a transaction object.
     *
     * @param userAcc  A user account
     * @param atmAcc   A atm account
     * @param quantity Quantity used in transaction.
     * @param typeStr String representing the transaction
     */
    protected Transaction(UserAccount userAcc, ATMAccount atmAcc, float quantity, String typeStr) {
        this.atmAccount = atmAcc;
        this.userAccount = userAcc;
        this.result = TransactionResult.INIT;
        this.quantity = quantity;
        this.transactionId = "not an id";
        this.typeStr = typeStr;
    }

    public ATMAccount getAtmAccount() {
        return this.atmAccount;
    }

    public TransactionDB getTransactionDB() {
        return Transaction.db;
    }

    public UserAccount getUserAccount() {
        return this.userAccount;
    }

    public TransactionResult getTransactionResult() {
        return this.result;
    }

    public TransactionType getTransactionType() {
        return this.type;
    }

    public void setTransactionResult(TransactionResult result) {
        this.result = result;
    }

    public void setTransactionType(TransactionType type) {
        this.type = type;
    }

    public double getQuantity() {
        return this.quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public String getTransactionId() {
        return this.transactionId;
    }

    public String getTypeStr() {
        return typeStr;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    /**
     * Records a successful transaction in the TransactionDB
     * @return True if the transaction was successfully stored, false otherwise
     */
    public boolean recordTransaction() {
        // TODO: Allow for failed transactions and store in DB
        setTransactionResult(TransactionResult.SUCCESS);
        setTransactionId(Long.toString(new Date().getTime()));
        try {
            getTransactionDB().insertTransaction(this);
            return true;
        } catch (SQLException e) {
            getTransactionDB().printSQLException(e);
            return false;
        }
    }

    abstract public boolean attempt() throws ATMException;

    /**
     * Checks if the UserAccount for this transaction is actually for the ATM Account.
     *
     * Checks by checking it the UserAccount id is the same as the ATM ID string
     * @return
     */
    public boolean isUserTransactionATM() {
        if (this.userAccount == null) {
            return false;
        }
        return Objects.equals(this.userAccount.getId(), Account.ATM_ACCOUNT_ID);
    }

}
