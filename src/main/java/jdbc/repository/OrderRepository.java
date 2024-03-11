package jdbc.repository;


import jdbc.db.ConnectionManager;
import jdbc.db.Operation;
import jdbc.entity.Order;
import jdbc.repository.exception.RepositoryAccessException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class OrderRepository {

    private final ConnectionManager connectionManager;

    public OrderRepository(ConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }

    private <T> T doOperation(Operation<T> operation) {
        Connection connection = connectionManager.getConnection();
        T result;
        try (connection) {
            result = operation.run(connection);
            connection.commit();
        } catch (Exception e) {
            try {
                connection.rollback();
            } catch (SQLException ignored) {}
            throw new RepositoryAccessException(e);
        }
        return result;
    }

    public List<Order> getAll() {
        return doOperation(connection -> {
            List<Order> list = new ArrayList<>();
            Statement stmt = connection.createStatement();
            ResultSet result = stmt.executeQuery("SELECT * FROM orders ORDER BY id ASC");
            while (result.next()) {
                list.add(new Order(result.getLong(1),
                        result.getString(2),
                        result.getString(3),
                        result.getDate(4).toLocalDate(),
                        result.getInt(5))
                );
            }
            return list;
        });
    }

    public Optional<Order> get(long id) {
        return doOperation(connection -> {
            try (PreparedStatement stmt = connection.prepareStatement("SELECT * FROM orders WHERE id=?")) {
                stmt.setLong(1, id);
                try (ResultSet result = stmt.executeQuery()) {
                    if (result.next()) {
                        return Optional.of(new Order(result.getLong(1),
                                result.getString(2),
                                result.getString(3),
                                result.getDate(4).toLocalDate(),
                                result.getInt(5)
                        ));
                    } else {
                        return Optional.empty();
                    }
                }
            }
        });
    }

    public void create(Order order) {
        doOperation(connection -> {
            try (PreparedStatement pstmt = connection.prepareStatement("INSERT INTO orders (`name`, `description`, `delivery_date`, `price`) VALUES(?, ?, ?, ?)");
                 Statement stmt = connection.createStatement()) {
                pstmt.setString(1, order.getName());
                pstmt.setString(2, order.getDescription());
                pstmt.setDate(3, Date.valueOf(order.getDeliveryDate()));
                pstmt.setInt(4, order.getPrice());
                pstmt.execute();

                try (ResultSet result = stmt.executeQuery("SELECT last_insert_rowid()")) {
                    if (result.next()) {
                        order.setId(result.getLong(1));
                    }
                }
            }
            return Optional.empty();
        });
    }

    public void update(Order order) {
        doOperation(connection -> {
            try (PreparedStatement pstmt = connection.prepareStatement("UPDATE orders SET `name`=?, `description`=?, `delivery_date`=?, `price`=? WHERE id=?")) {
                pstmt.setString(1, order.getName());
                pstmt.setString(2, order.getDescription());
                pstmt.setDate(3, Date.valueOf(order.getDeliveryDate()));
                pstmt.setInt(4, order.getPrice());
                pstmt.setLong(5, order.getId());
                pstmt.execute();
            }
            return Optional.empty();
        });
    }

    public void delete(long id) {
        doOperation(connection -> {
            try (PreparedStatement pstmt = connection.prepareStatement("DELETE FROM `orders` WHERE `id`=?")) {
                pstmt.setLong(1, id);
                pstmt.execute();
            }
            return Optional.empty();
        });
    }

    public void seedData(List<Order> orders) {
        doOperation(connection -> {
            Statement stmt = null;
            PreparedStatement pstmt = null;
            try {
                stmt = connection.createStatement();
                stmt.execute("CREATE TABLE IF NOT EXISTS `orders` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                        " `name` CHAR(120) NOT NULL," +
                        " `description` CHAR(1000) NOT NULL," +
                        " `delivery_date` INT NOT NULL," +
                        " `price` INT NOT NULL DEFAULT 0)");
                stmt.close();

                stmt = connection.createStatement();
                stmt.execute("DELETE FROM `orders`");
                stmt.close();

                stmt = connection.createStatement();
                stmt.execute("UPDATE `sqlite_sequence` SET `seq`=0 WHERE `name`='orders'");
                stmt.close();

                for (Order order : orders) {
                    pstmt = connection.prepareStatement("INSERT INTO orders VALUES(?, ?, ?, ?, ?)");
                    pstmt.setLong(1, order.getId());
                    pstmt.setString(2, order.getName());
                    pstmt.setString(3, order.getDescription());
                    pstmt.setDate(4, Date.valueOf(order.getDeliveryDate()));
                    pstmt.setInt(5, order.getPrice());
                    pstmt.execute();
                    pstmt.close();
                }

                connection.commit();
            } catch (Exception e) {
                throw new RepositoryAccessException(e);
            } finally {
                try {
                    if (stmt != null) stmt.close();
                    if (pstmt != null) pstmt.close();
                } catch (SQLException ignored) {
                }
            }
            return Optional.empty();
        });
    }

}
