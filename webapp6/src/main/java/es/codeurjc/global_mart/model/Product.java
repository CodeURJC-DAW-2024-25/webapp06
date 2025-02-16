package es.codeurjc.global_mart.model;


public class Product {

        private Long id;
        private String name;
        private String description;
        private String type;
        private float price;
        private String image;
        private int stock;

public Long getId() {
        return id;
}

public void setId(Long id) {
        this.id = id;
}

public String getName() {
        return name;
}

public void setName(String name) {
        this.name = name;
}

public String getDescription() {
        return description;
}

public void setDescription(String description) {
        this.description = description;
}

public String getType() {
        return type;
}

public void setType(String type) {
        this.type = type;
}

public float getPrice() {
        return price;
}

public void setPrice(float price) {
        this.price = price;
}

public String getImage() {
        return image;
}

public void setImage(String image) {
        this.image = image;
}

public int getStock() {
        return stock;
}

public void setStock(int stock) {
        this.stock = stock;
}

public Product(Long id, String name, String description, String type, float price, String image, int stock) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.type = type;
        this.price = price;
        this.image = image;
        this.stock = stock;      
}

public Product() {}
}