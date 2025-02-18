package es.codeurjc.global_mart.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import es.codeurjc.global_mart.repository.OrderRepository;
import es.codeurjc.global_mart.model.Order;


@Service
public class OrderService {
    
    @Autowired
    private OrderRepository orderRepository;

    public Order createOrder(){
        Order order = new Order();
        return orderRepository.save(order);
    }




}
