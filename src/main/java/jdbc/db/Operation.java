package jdbc.db;

import java.sql.Connection;
import java.sql.SQLException;

public interface Operation<T> {
    T run(Connection connection) throws SQLException;
}
