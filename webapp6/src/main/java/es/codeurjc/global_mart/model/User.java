package es.codeurjc.global_mart.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "USERS")
public class User {

    // ----------------- Atributes -----------------
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String email;
    private String password;
    private Boolean isCompany;
    private List<String> role;

    @OneToMany
    private List<Order> orders;

    @OneToMany
    private List<Review> reviews;

    // ----------------- Constructor -----------------
    public User() {
    }

    public User(String username, String email, String password, Boolean isCompany, List<String> role) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.isCompany = isCompany;
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


    public boolean getIsCompany() {
        return isCompany;
    }

    public List<String> getRole() {
        return role;
    }

    public List<Review> getReviews() {
        return reviews;
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

    public void setIsCompany(Boolean isCompany) {
        this.isCompany = isCompany;
    }


    public void addReview(Review review) {
        this.reviews.add(review);
    }

    public void removeReview(Review review) {
        this.reviews.remove(review);
    }

}
