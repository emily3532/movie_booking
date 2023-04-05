package ATM.Database;

import java.sql.*;

import ATM.Entities.Transactions.Transaction;


public class TransactionDB extends Database {


    /**
     * Insert NEW transaction into DB
     *
     * @param   t the transaction to insert
     */
    public void insertTransaction(Transaction t) throws SQLException {
        String query = "insert into transaction values ('" + t.getUserAccount().getId() + "', '" + t.getQuantity() +
        "','" +  t.getTransactionId() + "', '" + t.getTypeStr() + "', '" + getTime() + "');";
        modify(query);
    }


}