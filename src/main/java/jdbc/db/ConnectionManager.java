package jdbc.db;

import java.sql.*;

public class ConnectionManager {
    private Connection connection;

    public ConnectionManager(String conn) throws SQLException {
        connection = DriverManager.getConnection(conn);
    }

    public Connection getConnection() {
        return connection;
    }
}
