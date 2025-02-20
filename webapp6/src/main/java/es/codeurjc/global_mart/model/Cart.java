package es.codeurjc.global_mart.model;

import java.util.ArrayList;

import org.hibernate.metamodel.mapping.internal.EntityCollectionPart.Cardinality;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import es.codeurjc.global_mart.model.User;



@Entity
@Table(name = "CART")

public class Cart{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private ArrayList<Product> productsList;

    private int productsQuantity;
    private Double totalPrice;

    @OneToOne
    private User user;


    
    

    // ----------------- Constructor -----------------
    public Cart() {
        this.productsList = new ArrayList<Product>();
        this.totalPrice = 0.0;
        this.productsQuantity = 0;
    }

    public Cart(User user){
        this();                 // Call the default constructor
        this.user = user;
    }

    // ----------------- Methods -----------------
    public void addProduct(Product product) {       // Add a product to the cart and actualize the total price
        productsList.add(product);
        totalPrice += product.getPrice();
        productsQuantity++;
    }

    public void deleteProduct(Product product){           //remove a product from the cart
        productsList.remove(product);
        totalPrice -= product.getPrice();
        productsQuantity --;
    }





}


