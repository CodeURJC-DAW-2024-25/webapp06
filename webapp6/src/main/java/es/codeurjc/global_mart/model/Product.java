package es.codeurjc.global_mart.model;

import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;

@Entity
@Table(name = "PRODUCTS")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String type;
    private String name;
    private String business;
    private Double price;
    private String description;
    private String image;

    @ManyToMany(mappedBy = "products")
    private List<Order> orders;

    // ----------------- Constructor -----------------
    public Product() {
    }

    public Product(String type, String name, String business, Double price, String description, String image) {
        this.type = type;
        this.name = name;
        this.business = business;
        this.price = price;
        this.description = description;
        this.image = image;
    }

    // ----------------- Methods -----------------
    // !Getters
    public Long getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public String getBusiness() {
        return business;
    }

    public Double getPrice() {
        return price;
    }

    public String getDescription() {
        return description;
    }

    public String getImage() {
        return image;
    }

    // !Setters
    public void setId(Long id) {
        this.id = id;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setBusiness(String business) {
        this.business = business;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setImage(String image) {
        this.image = image;
    }

}
