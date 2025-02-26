package es.codeurjc.global_mart.model;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToMany;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;

import java.util.ArrayList;
import java.util.List;

import java.sql.Blob;

@Entity
@Table(name = "USERS")
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
    private String password;

    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> role;

    @OneToMany
    private List<Order> orders;

    @OneToMany
    private List<Review> reviews;

    // ----------------- Constructor -----------------
    public User() {
    }

    public User(String name, String username, String email, String password, List<String> role) {
        this.name = name;
        this.username = username;
        this.email = email;
        this.password = password;
        this.orders = new ArrayList<>();
        this.reviews = new ArrayList<>();
        this.role = role;
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
        return password;
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
        this.password = password;
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
}
