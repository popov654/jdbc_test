package jdbc;

import jdbc.entity.Order;
import jdbc.repository.OrderRepository;
import jdbc.repository.exception.RepositoryErrorException;
import jdbc.repository.exception.ResultNotFoundException;
import jdbc.service.OrderService;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.time.LocalDate;

/**
 *
 * @author Alex
 */
public class Main {

    public final static String DATA_SOURCE = "jdbc:sqlite:orders.db";

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
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(DATA_SOURCE);
            OrderService service = new OrderService(new OrderRepository(conn));
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
        } catch (SQLException | RepositoryErrorException | ResultNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
