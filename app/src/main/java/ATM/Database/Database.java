package ATM.Database;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public abstract class Database {
    /**
     * Prints the SQL Exceptions from the DB
     *
     */
    public static void printSQLException(SQLException ex) {
        for (Throwable e : ex) {
            if (e instanceof SQLException) {
                e.printStackTrace(System.err);
                System.err.println("SQLState: " + ((SQLException) e).getSQLState());
                System.err.println("Error Code: " + ((SQLException) e).getErrorCode());
                System.err.println("Message: " + e.getMessage());
                Throwable t = ex.getCause();
                while (t != null) {
                    System.out.println("Cause: " + t);
                    t = t.getCause();
                }
            }
        }
    }

    /**
     * Creates a connection to the database
     *
     * @return The connection on success or null otherwise
     */
    public static Connection getConnection() throws SQLException {
        try {
            Connection conn = DriverManager.getConnection(
                    "jdbc:postgresql://database-1.c5yqcscphzk5.us-east-1.rds.amazonaws.com:5432/",
                    "postgres",
                    "FvZ7_pnM");
            return conn;
        } catch (SQLException e) {
            printSQLException(e);
            throw new SQLException("Failed to connect to database");
        }
    }


    /**
     * Select statement
     *
     * @param query The query string
     * @return The result set, or null if empty
     */
    public static ResultSet select(String query) throws SQLException  {
        Connection conn = getConnection();

        try (conn) {
            PreparedStatement s = conn.prepareStatement(query);
            ResultSet res = s.executeQuery(query);

            if (!res.isBeforeFirst()) {
                throw new SQLException("Your card and/or PIN number is incorrect.");
            }
            conn.close();
            return res;
        } catch (SQLException e) {
            printSQLException(e);
            throw e;
        }
    }

    /**
     * Select where statement
     *
     * @param query The query string
     * @return The result set, or null if empty
     */
    public static ResultSet selectwhere(String query, String params) throws SQLException  {
        Connection conn = getConnection();

        try (conn) {
            PreparedStatement s = conn.prepareStatement(query);
            s.setString(1, params);
            ResultSet res = s.executeQuery(query);

            if (!res.isBeforeFirst()) {
                throw new SQLException("Your card and/or PIN number is incorrect.");
            }
            conn.close();
            return res;
        } catch (SQLException e) {
            printSQLException(e);
            throw e;
        }
    }


    /**
     * Executes a Statement which modifies the database
     *
     * @param query The query string
     * @return The number of rows modified or -1 on failure
     */
    public static int modify(String query, String ... params) throws SQLException  {

        System.out.println("Query:" + query);
        int i = 1;
        Connection conn = getConnection();

        try (conn) {
            PreparedStatement s = conn.prepareStatement(query);

            for(String p: params){
                s.setString(i, p);
                i++;
                System.out.println(p);
            }

            int res = s.executeUpdate();
            conn.close();
            return res;

        } catch (SQLException e) {
            printSQLException(e);
            throw e;
        }
    }


    public static LocalDateTime getLocalDate(String dateStr){
        if (dateStr == null) {
            return null;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-YYYY");
        LocalDateTime ld = LocalDateTime.parse(dateStr, formatter);
        return ld;
    }
    public String getTime(){
        Date time = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        return formatter.format(time);
    }
}
