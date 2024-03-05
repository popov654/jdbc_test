package jdbc.repository;


import jdbc.db.ConnectionManager;
import jdbc.entity.Order;
import jdbc.repository.exception.RepositoryAccessException;
import jdbc.repository.exception.ResultNotFoundException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderRepository {

    private final ConnectionManager connectionManager;

    public OrderRepository(ConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }

    public List<Order> getAll() throws RepositoryAccessException {
        try (Connection connection = connectionManager.getConnection()) {
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
        } catch (Exception e) {
            throw new RepositoryAccessException(e);
        }
    }

    public Order get(long id) throws RepositoryAccessException, ResultNotFoundException {
        try (Connection connection = connectionManager.getConnection()) {
            PreparedStatement stmt = connection.prepareStatement("SELECT * FROM orders WHERE id=?");
            stmt.setLong(1, id);
            ResultSet result = stmt.executeQuery();
            if (result.next()) {
                return new Order(result.getLong(1),
                        result.getString(2),
                        result.getString(3),
                        result.getDate(4).toLocalDate(),
                        result.getInt(5)
                );
            } else {
                throw new ResultNotFoundException(String.format("Order with ID %d not found", id));
            }
        } catch (Exception e) {
            throw new RepositoryAccessException(e);
        }
    }

    public void create(Order order) throws RepositoryAccessException {
        Connection connection = connectionManager.getConnection();
        try (connection) {
            PreparedStatement pstmt = connection.prepareStatement("INSERT INTO orders (`name`, `description`, `delivery_date`, `price`) VALUES(?, ?, ?, ?)");
            pstmt.setString(1, order.getName());
            pstmt.setString(2, order.getDescription());
            pstmt.setDate(3, Date.valueOf(order.getDeliveryDate()));
            pstmt.setInt(4, order.getPrice());
            pstmt.execute();

            Statement stmt = connection.createStatement();
            ResultSet result = stmt.executeQuery("SELECT last_insert_rowid()");
            if (result.next()) {
                order.setId(result.getLong(1));
            }

            connection.commit();
        } catch (Exception e) {
            try {
                connection.rollback();
            } catch (SQLException ex) {
                throw new RepositoryAccessException(ex);
            }
            throw new RepositoryAccessException(e);
        }
    }

    public void update(Order order) throws RepositoryAccessException {
        Connection connection = connectionManager.getConnection();
        try (connection) {
            PreparedStatement pstmt = connection.prepareStatement("UPDATE orders SET `name`=?, `description`=?, `delivery_date`=?, `price`=? WHERE id=?");
            pstmt.setString(1, order.getName());
            pstmt.setString(2, order.getDescription());
            pstmt.setDate(3, Date.valueOf(order.getDeliveryDate()));
            pstmt.setInt(4, order.getPrice());
            pstmt.setLong(5, order.getId());
            pstmt.execute();

            connection.commit();
        } catch (Exception e) {
            try {
                connection.rollback();
            } catch (SQLException ex) {
                throw new RepositoryAccessException(ex);
            }
            throw new RepositoryAccessException(e);
        }
    }

    public void delete(long id) throws RepositoryAccessException {
        Connection connection = connectionManager.getConnection();
        try (connection) {
            PreparedStatement pstmt = connection.prepareStatement("DELETE FROM `orders` WHERE `id`=?");
            pstmt.setLong(1, id);
            pstmt.execute();

            connection.commit();
        } catch (Exception e) {
            try {
                connection.rollback();
            } catch (SQLException ex) {
                throw new RepositoryAccessException(ex);
            }
            throw new RepositoryAccessException(e);
        }
    }

    public void seedData(List<Order> orders) throws RepositoryAccessException {
        Connection connection = connectionManager.getConnection();
        try (connection) {
            Statement stmt = connection.createStatement();
            stmt.execute("CREATE TABLE IF NOT EXISTS `orders` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                    " `name` CHAR(120) NOT NULL," +
                    " `description` CHAR(1000) NOT NULL," +
                    " `delivery_date` INT NOT NULL," +
                    " `price` INT NOT NULL DEFAULT 0)");

            stmt = connection.createStatement();
            stmt.execute("DELETE FROM `orders`");

            stmt = connection.createStatement();
            stmt.execute("UPDATE `sqlite_sequence` SET `seq`=0 WHERE `name`='orders'");

            for (Order order : orders) {
                PreparedStatement pstmt = connection.prepareStatement("INSERT INTO orders VALUES(?, ?, ?, ?, ?)");
                pstmt.setLong(1, order.getId());
                pstmt.setString(2, order.getName());
                pstmt.setString(3, order.getDescription());
                pstmt.setDate(4, Date.valueOf(order.getDeliveryDate()));
                pstmt.setInt(5, order.getPrice());
                pstmt.execute();
            }

            connection.commit();
        } catch (Exception e) {
            try {
                connection.rollback();
            } catch (SQLException ex) {
                throw new RepositoryAccessException(ex);
            }
            throw new RepositoryAccessException(e);
        }
    }

}
