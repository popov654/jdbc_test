package jdbc;

import jdbc.db.ConnectionManager;
import jdbc.entity.Order;
import jdbc.repository.OrderRepository;
import jdbc.repository.exception.RepositoryAccessException;
import jdbc.repository.exception.ResultNotFoundException;
import jdbc.service.OrderService;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.time.LocalDate;

/**
 *
 * @author Alex
 */
public class Main {

    public final static String DATA_SOURCE = "jdbc:sqlite:orders.db";
    private static ConnectionManager connectionManager;

    public static void printOrders(List<Order> orders) {
        System.out.println("Orders list:");
        for (Order order: orders) {
            System.out.printf("ID: %d Name: %s Price: %d%n", order.getId(), order.getName(), order.getPrice());
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            connectionManager = new ConnectionManager(DATA_SOURCE);
            OrderService service = new OrderService(new OrderRepository(connectionManager));
            service.seedData();
            printOrders(service.getOrders());

            System.out.println();
            System.out.print("Creating new order... ");
            Order order = service.createOrder("New order", "My description", LocalDate.of(2024, 3, 1), 1250);
            System.out.printf("Order with ID %d was created.%n", order.getId());

            long idToChange = 4L;
            int newPrice = 2700;
            System.out.printf("Setting order %d price to %d... ", idToChange, newPrice);
            order = service.getOrder(idToChange);
            order.setPrice(newPrice);
            service.updateOrder(order);
            System.out.println("Done");

            long idToRemove = 3;
            System.out.printf("Removing order %d... ", idToRemove);
            service.removeOrder(idToRemove);
            System.out.println("Done");

            System.out.println();
            printOrders(service.getOrders());
        } catch (SQLException | RepositoryAccessException | ResultNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                Connection conn = connectionManager.getConnection();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
