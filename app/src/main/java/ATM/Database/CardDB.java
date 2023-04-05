package ATM.Database;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

import ATM.Entities.Accounts.UserAccount;
import ATM.Entities.Card;

/**
 * Card specific database class
 *
 */
public class CardDB extends Database {

    /**
     * Insert NEW card into DB
     *
     * @param   cardnum the card number
     * @param   pin the card pin
     * @param   issuedate   date card issued
     * @param   expiredate  date card expires
     * @throws SQLException If any DB exception occurs
     */
    public void insertCard(String cardnum, String pin, String issuedate, String expiredate) throws SQLException {
        String query = "insert into Card values (" + cardnum + ", " + pin + ", '" +  issuedate + "', '" + expiredate + "', 'OK');";
        System.out.println("Inserting a card");
        modify(query);
    }


    /**
     * Links NEW card with account in DB
     *
     * @param   cardnum the card number
     * @param   accountnum the account number
     * @throws SQLException If any DB exception occurs
     */
    public void linkCardAccount(String cardnum, String accountnum) throws SQLException {
        String query = "insert into Card_Account values (" + cardnum + ", " + accountnum + ");";
        System.out.println("Linking a card");
        modify(query);
    }

    /**
     * Finds accounts linked to card in DB
     *
     * @param   cardnum the card number
     * @return list of strings of linked accounts
     * @throws SQLException If any DB exception occurs
     */
    public List<String> retrieveLinkedAccounts(String cardnum) throws SQLException {
        String query = "SELECT accountNum\n" +
            "FROM Card_Account\n" +
            "Where cardNum = '" + cardnum + "'\n" +
            "Order by accountNum";

        ResultSet res = select(query);

        List<String> ls = new ArrayList<String>();
        while (res.next()){
            ls.add(res.getString("accountNum"));
        }
        return ls;
    
    }


    /**
     * Retrieves card instance from db
     *
     * @param  cardnum   the card number
     * @return Card instance of specified card number
     * @throws SQLException If any DB exception occurs
     */
    public Card retrieveCard(String cardnum) throws SQLException {
        String query = "select cardNum, pin, issuedate, expiredate, state from Card where cardNum = '" + cardnum + "'";
        ResultSet res = select(query);


        while(res.next()) {
            String pin = res.getString("pin");
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            LocalDate issue = LocalDate.parse(res.getString("issuedate"), formatter);
            LocalDate expr = LocalDate.parse(res.getString("expiredate"), formatter);

            String state = res.getString("state");
            Card.Status status = null;
            switch (state) {
                case "OK":
                    status = Card.Status.OK;
                    break;
                case "BLOCKED":
                    status = Card.Status.BLOCKED;
                    break;
                case "STOLEN":
                    status = Card.Status.STOLEN;
                    break;
                case "LOST":
                    status = Card.Status.LOST;
                    break;
                default:
                    status = null;
                    break;

            }
            return new Card(cardnum, pin, status, issue, expr);

        }

        throw new SQLException("We should not be here. Empty results set");
    }


    /**
     * update card into DB
     *
     * @param   cardnum the card number
     * @param   pin the card pin
     * @param   issuedate   date card issued
     * @param   expiredate  date card expires
     * @param   state       state
     * @throws SQLException If any DB exception occurs
     */
    public void updateAccount(String cardnum, String pin, String issuedate, String expiredate, String state) throws SQLException {
        String query = "UPDATE Card\n" +
        "SET Pin = '" + pin +
        "', issuedate = '" + issuedate +
        "', expiredate = '" + expiredate +
        "', state = '" + state +
        "'\n" +
        "WHERE cardNum = '" + cardnum +
        "'";
        modify(query);

    }
}