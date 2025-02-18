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
        return orderRepository.findById(id).orElseThrow(() -> new RuntimeException("Order not found with id " + id));
    }

    


}
