package es.codeurjc.global_mart.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import es.codeurjc.global_mart.repository.OrderRepository;
import es.codeurjc.global_mart.model.User;
import es.codeurjc.global_mart.model.Order;
import es.codeurjc.global_mart.repository.ProductRepository;
import es.codeurjc.global_mart.model.Product;
import es.codeurjc.global_mart.repository.UserRepository;
// import es.codeurjc.global_mart.model.Product;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    public Order createOrder(User user) {
        Order order = new Order(user.getUsername(), user.getTotalPrice(), user, new ArrayList<>(user.getCart()));

        user.getHistoricalOrderPrices().add(order.getTotal());
        user.emptyCart();
        user.getOrders().add(order);
        System.out.println("historicalOrderPrices: " + user.getHistoricalOrderPrices());

        userRepository.save(user);
        orderRepository.save(order);
        return order;
    }

    public List<Order> findAllOrders() {
        return orderRepository.findAll();
    }

    public Order findOrderById(Long id) {

        try {
            return orderRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Order not found with id " + id));
        } catch (Exception e) {
            throw new RuntimeException("Error finding order with id " + id, e);
        }

    }

    public Order addProductToOrder(Long orderId, Long productId) {
        try {
            Order order = orderRepository.findById(orderId)
                    .orElseThrow(() -> new RuntimeException("Order not found with id " + orderId));
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new RuntimeException("Product not found with id " + productId));
            order.addProduct(product);
            return orderRepository.save(order);
        } catch (Exception e) {
            throw new RuntimeException("Error adding product to order with id " + orderId, e);
        }

    }

    public Order deleteProductFromOrder(Long orderId, Long productId) {
        try {
            Order order = orderRepository.findById(orderId)
                    .orElseThrow(() -> new RuntimeException("Order not found with id " + orderId));
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new RuntimeException("Product not found with id " + productId));
            order.deleteProduct(product);
            return orderRepository.save(order);
        } catch (Exception e) {
            throw new RuntimeException("Error deleting product from order with id " + orderId, e);
        }

    }

}
