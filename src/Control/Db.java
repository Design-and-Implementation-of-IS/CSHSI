package Control;
import java.sql.*;
import Entity.Consts;

public class Db {
    public static Connection get() throws SQLException {
        return DriverManager.getConnection(Consts.CONN_STR);
    }
}