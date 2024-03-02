package jdbc.db;

import java.sql.*;

public class ConnectionManager {

    private Connection connection;
    private String connectionString;

    public ConnectionManager(String conn) throws SQLException {
        connectionString = conn;
        connection = DriverManager.getConnection(conn);
    }

    public Connection getConnection() {
        try {
            if (connection == null || !connection.isValid(1)) {
                connection = DriverManager.getConnection(connectionString);
            }
        } catch (SQLException e) {}
        return connection;
    }
}
