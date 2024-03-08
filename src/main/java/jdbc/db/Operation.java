package jdbc.db;

import java.sql.Connection;

public interface Operation<T> {
    T run(Connection connection);
}
