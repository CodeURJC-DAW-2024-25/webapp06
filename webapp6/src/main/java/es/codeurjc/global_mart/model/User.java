package es.codeurjc.global_mart.model;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;

import java.util.ArrayList;
import java.util.List;

import java.sql.Blob;

@Entity(name = "Users")
public class User {

    // ----------------- Atributes -----------------
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Lob
    private Blob image;

    private String name;
    private String username;
    private String email;
    private String encodedPassword;

    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> role;

    @OneToMany
    private List<Order> orders;

    @OneToMany
    private List<Review> reviews;

    @OneToMany
    private List<Product> cart;

    // ----------------- Constructor -----------------
    public User() {
    }

    public User(String name, String username, String email, String password, List<String> role) {
        this.name = name;
        this.username = username;
        this.email = email;
        this.encodedPassword = password;
        this.orders = new ArrayList<>();
        this.reviews = new ArrayList<>();
        this.role = role;
        this.cart = new ArrayList<>();

    }

    public boolean isAdmin() {
        return role.contains("ADMIN");
    }
    public boolean isCompany() {
        return role.contains("COMPANY");
    }
    public boolean isUser() {
        return role.contains("USER");
    }

    // ----------------- Methods -----------------
    // !Getters
    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public List<Order> getOrders() {
        return orders;
    }

    public String getPassword() {
        return encodedPassword;
    }

    public List<String> getRole() {
        return role;
    }

    public List<Review> getReviews() {
        return reviews;
    }

    public Blob getImage() {
        return image;
    }

    public String getName() {
        return name;
    }

    public List<Product> getCart() {
        return cart;
    }

    // !Setters
    public void setId(Long id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setRole(List<String> role) {
        this.role = role;
    }

    public void setPassword(String password) {
        this.encodedPassword = password;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }

    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
    }

    public void setImage(Blob image) {
        this.image = image;
    }

    public void setName(String name) {
        this.name = name;
    }

    // Reviews
    public void addReview(Review review) {
        this.reviews.add(review);
    }

    public void removeReview(Review review) {
        this.reviews.remove(review);
    }

    // Cart methods

    public void addProductToCart(Product product) {
        this.cart.add(product);
    }

    public void removeProductFromCart(Product product) {
        this.cart.remove(product);
    }

    public void emptyCart() {
        this.cart.clear();
    }  
}

