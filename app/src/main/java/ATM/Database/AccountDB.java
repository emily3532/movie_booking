package ATM.Database;

import ATM.Entities.Accounts.Account;
import ATM.Entities.Accounts.UserAccount;
import ATM.Entities.Accounts.ATMAccount;

import java.sql.*;

public class AccountDB extends Database {
    /**
     * Insert NEW account into DB
     *
     * @param   accountNum the account number
     * @param   balance     starting balance?
     */
    public void insertAccount(String accountNum, double balance) throws SQLException {
        System.out.println("Inserting an account");
        String query = "insert into account values (" + accountNum + ", " + balance + ");";
        modify(query);
    }

    /**
     * Deletes account instance from db
     *
     * @param  n   the account number
     */
    public void deleteAccount(String n) throws SQLException {
        System.out.println("Deleting an Account");
        String query = "delete from account where accountNum = '" + n +"'";
        modify(query);
    }

    /**
     * update account in DB
     *
     * @param   acc The Account state to put in database
     */
    public void updateAccount(Account acc) throws SQLException{
        System.out.println("Updating an Account");
        if (acc == null) {
            return;
        }
        String query = "UPDATE account\n" +
        "SET balance = '" + acc.getBalance() +
        "'\n" +
        "WHERE accountNum = '" + acc.getId() +
        "'";
        modify(query);

    }

    /**
     * Retrieves account instance from db
     *
     * @param   n   the account number
     */
    public UserAccount retrieveUserAccount(String n) {
        ResultSet res = null;

        String query = "select accountNum, balance from account where accountNum = '" + n +"'";


        try {
            res = select(query);

            if (res == null) {
                return null;
            }
            while(res.next()){
                String id = res.getString("accountNum");
                double balance = res.getDouble("balance");
                return new UserAccount(id, balance);
            }

        } catch (SQLException e) {
            System.out.println("Unable to unpack user account result");
            printSQLException(e);
        }

        return null;
    }

        /**
     * Retrieves account instance from db
     *
     * @param   n   the account number
     */
    public ATMAccount retrieveATMAccount(String n) {
        ResultSet res = null;
        String query = "select accountNum, balance from account where accountNum = '" + n +"'";

        try {
            res = select(query);

            if (res == null) {
                return null;
            }

            while(res.next()){
                String id = res.getString("accountNum");
                double balance = res.getDouble("balance");
                return new ATMAccount(id, balance);
            }


        } catch (SQLException e) {
            System.out.println("Unable to unpack atm account result");
            printSQLException(e);
        }

        return null;
    }

} 