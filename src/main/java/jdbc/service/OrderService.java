package jdbc.service;

import jdbc.entity.Order;
import jdbc.repository.OrderRepository;
import jdbc.repository.exception.RepositoryAccessException;
import jdbc.repository.exception.ResultNotFoundException;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

/**
 *
 * @author Alex
 */
public class OrderService {

    private final OrderRepository repository;

    public OrderService(OrderRepository repository) {
        this.repository = repository;
    }
    
    public void seedData() throws RepositoryAccessException {
        List<Order> orders = List.of(
                new Order(1L, "Order 1", "Order description", LocalDate.of(2023, 7, 4), 2200),
                new Order(2L, "Order 2", "Order description", LocalDate.of(2022, 10, 17),1760),
                new Order(3L, "Order 3", "Order description", LocalDate.of(2024, 1, 23),1148),
                new Order(4L, "Order 4", "Order description", LocalDate.of(2022, 9, 8),3250),
                new Order(5L, "Order 5", "Order description", LocalDate.of(2022, 5, 16),3390),
                new Order(6L, "Order 6", "Order description", LocalDate.of(2023, 8, 19),1550)
        );
        try {
            this.repository.seedData(orders);
        } catch (SQLException e) {
            throw new RepositoryAccessException(e);
        }
    }

    public List<Order> getOrders() throws RepositoryAccessException {
        try {
            return repository.getAll();
        } catch (SQLException e) {
            throw new RepositoryAccessException(e);
        }
    }

    public Order getOrder(long id) throws RepositoryAccessException, ResultNotFoundException {
        try {
            return repository.get(id);
        } catch (SQLException e) {
            throw new RepositoryAccessException(e);
        }
    }

    public Order createOrder(String name, String description, LocalDate deliveryDate, int price) throws RepositoryAccessException {
        Order order = new Order(0L, name, description, deliveryDate, price);
        try {
            repository.create(order);
            return order;
        } catch (SQLException e) {
            throw new RepositoryAccessException(e);
        }
    }

    public void updateOrder(Order order) throws RepositoryAccessException {
        try {
            repository.update(order);
        } catch (SQLException e) {
            throw new RepositoryAccessException(e);
        }
    }

    public void removeOrder(Order order) throws RepositoryAccessException {
        try {
            repository.delete(order);
        } catch (SQLException e) {
            throw new RepositoryAccessException(e);
        }
    }

    public void removeOrder(long id) throws RepositoryAccessException {
        try {
            repository.delete(id);
        } catch (SQLException e) {
            throw new RepositoryAccessException(e);
        }
    }

}
