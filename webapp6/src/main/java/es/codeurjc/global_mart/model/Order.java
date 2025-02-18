package es.codeurjc.global_mart.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import java.util.List;
import java.util.ArrayList;

@Entity
@Table(name = "ORDERS")

public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double total; // Total price of the order

    private String direction; // Direction where the order will be sent

    private String userName;

    @ManyToMany
    private List<Product> products; // Our order have minum one product to n products

    // ----------------- Constructor -----------------
    public Order() {
        this.products = new ArrayList<Product>();
        this.total = 0.0;

    }

    public Order(List<Product> products, Double total, String direction, String userName) {
        this.products = products;
        this.total = total;
        this.direction = direction;
        this.userName = userName;
    }




    // ----------------- Methods -----------------

    public void addProduct(Product product) {       // Add a product to the order and actualize the total price
        products.add(product);
        total += product.getPrice();
    }

    public void deleteProduct(Product product) {    // Delete a product from the order and actualize the total price
        products.remove(product);
        total -= product.getPrice();
    }


    // !Getters
    public Long getId() {
        return id;
    }

    public List<Product> getProducts() {
        return products;
    }

    public Double getTotal() {
        return total;
    }

    public String getDirection() {
        return direction;
    }

    public String getUserName() {
        return userName;
    }

    // !Setters
    public void setProducts(List<Product> products) {
        this.products = products;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setId(Long id) {
        this.id = id;
    }

}
