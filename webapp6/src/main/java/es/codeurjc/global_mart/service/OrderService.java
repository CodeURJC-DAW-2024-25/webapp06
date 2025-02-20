package es.codeurjc.global_mart.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import es.codeurjc.global_mart.repository.OrderRepository;
import es.codeurjc.global_mart.model.Order;
// import es.codeurjc.global_mart.model.Product;

@Service
public class OrderService {
    
    @Autowired
    private OrderRepository orderRepository;

    public Order createOrder() {
        Order order = new Order();
        

        return orderRepository.save(order);
    }
    

    public List<Order> findAllOrders() {
        return orderRepository.findAll();
    }

    public Order findOrderById(Long id) {

        try {
            return orderRepository.findById(id);
        } catch (Exception e) {
            throw new RuntimeException("Error finding order with id " + id, e);
        }
        
    }

    public Order addProductToOrder(Long orderId, Long productId) {
        try {
            Order order = orderRepository.findById(orderId);
            Product product = productRepository.findById(productId);
            order.addProduct(product);
        } catch (Exception e) {
            throw new RuntimeException("Error adding product to order with id " + orderId, e);
        }

        return orderRepository.save(order);
    }

    public Order deleteProductFromOrder(Long orderId, Long productId) {
        try {
            Order order = orderRepository.findById(orderId);
            Product product = productRepository.findById(productId);
            order.deleteProduct(product);
        } catch (Exception e) {
            throw new RuntimeException("Error deleting product from order with id " + orderId, e);
        }

        return orderRepository.save(order);
    }
    


}
