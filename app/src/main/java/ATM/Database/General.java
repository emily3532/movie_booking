package ATM.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Time;
import java.util.Date;


public class General extends Database {

    public static void timeAdd() throws SQLException {
        try {
            Connection conn = getConnection();

            String query = "insert into timer values (?, ?)";
            PreparedStatement s = conn.prepareStatement(query);

            java.sql.Time sqlTime = new java.sql.Time(10,01,00);
            java.sql.Date sqlDate = new java.sql.Date(2019,01,01);
            s.setTime(1, sqlTime);
            s.setDate(2, sqlDate);
            int res = s.executeUpdate();
            conn.close();
            return;

        } catch (SQLException e) {
            printSQLException(e);
        }
    }




    public static void main(String[] args) throws SQLException {
        General d = new General();
        d.timeAdd();
    }
}

