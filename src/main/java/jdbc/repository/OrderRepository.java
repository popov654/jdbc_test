package jdbc.repository;


import jdbc.entity.Order;
import jdbc.repository.exception.ResultNotFoundException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderRepository {

    private final Connection connection;

    public OrderRepository(Connection connection) {
        this.connection = connection;
    }

    public List<Order> getAll() throws SQLException {
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
    }

    public Order get(long id) throws SQLException, ResultNotFoundException {
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
    }

    public void create(Order order) throws SQLException {
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
    }

    public void update(Order order) throws SQLException {
        PreparedStatement pstmt = connection.prepareStatement("REPLACE INTO orders (`id`, `name`, `description`, `delivery_date`, `price`) VALUES(?, ?, ?, ?, ?)");
        pstmt.setLong(1, order.getId());
        pstmt.setString(2, order.getName());
        pstmt.setString(3, order.getDescription());
        pstmt.setDate(4, Date.valueOf(order.getDeliveryDate()));
        pstmt.setInt(5, order.getPrice());
        pstmt.execute();
    }

    public void delete(Order order) throws SQLException {
        delete(order.getId());
    }

    public void delete(long id) throws SQLException {
        PreparedStatement pstmt = connection.prepareStatement("DELETE FROM `orders` WHERE `id`=?");
        pstmt.setLong(1, id);
        pstmt.execute();
    }

    public void seedData(List<Order> orders) throws SQLException {
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

        for (Order order: orders) {
            PreparedStatement pstmt = connection.prepareStatement("INSERT INTO orders VALUES(?, ?, ?, ?, ?)");
            pstmt.setLong(1, order.getId());
            pstmt.setString(2, order.getName());
            pstmt.setString(3, order.getDescription());
            pstmt.setDate(4, Date.valueOf(order.getDeliveryDate()));
            pstmt.setInt(5, order.getPrice());
            pstmt.execute();
        }
    }

}
