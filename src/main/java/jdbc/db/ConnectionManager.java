package jdbc.db;

import java.sql.*;

public class ConnectionManager {

    private Connection connection;
    private final String connectionString;

    public ConnectionManager(String conn) throws SQLException {
        connectionString = conn;
        connection = DriverManager.getConnection(conn);
    }

    public Connection getConnection() {
        try {
            int timeout = 1;
            if (connection == null || !connection.isValid(timeout)) {
                connection = DriverManager.getConnection(connectionString);
            }
            connection.setAutoCommit(false);
        } catch (SQLException e) {}
        return connection;
    }
}
