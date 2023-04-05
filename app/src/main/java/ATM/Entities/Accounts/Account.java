package ATM.Entities.Accounts;

import java.sql.SQLException;

import ATM.Database.AccountDB;

/**
 * Class containing methods to execute transaction requests
 */
public abstract class Account {

    public final String id;
    private double balance;
    private static AccountDB db = new AccountDB();
    public static final String ATM_ACCOUNT_ID = "ATMacc";
    public static final String ATM_ACCOUNT_CARDNUM = "admin"; //very secure lol

    protected Account(String id, double balance) {
        this.id = id;
        this.balance = balance;
    }

    public String getId() {
        return this.id;
    }

    public double getBalance() {
        return balance;
    }


    public AccountDB getAccountDB() {
        return db;
    }

    public static void setDB(AccountDB db) {
        Account.db = db;
    }

    /**
     * Changes the balance of an account object without overdrawing
     *
     * @param inc Value by which to increment a balance by
     * @return True if balance changed, false otherwise
     */
    public boolean changeBalance(double inc) {
        if (balance + inc < 0) {
            return false;
        }

        try {
            balance = balance + inc;
            getAccountDB().updateAccount(this);
            return true;
        } catch (SQLException e) {
            balance = balance - inc;
            getAccountDB().printSQLException(e);
            return false;
        }
    }


    public static ATMAccount retrieveAtmAccount(String n) { return db.retrieveATMAccount(n); }

    public static UserAccount retrieveUserAccount(String n) {
        return db.retrieveUserAccount(n);
    }
}

