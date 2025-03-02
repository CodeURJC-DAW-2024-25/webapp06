package es.codeurjc.global_mart.repository;

import es.codeurjc.global_mart.model.Product;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByType(String type);

    List<Product> findByNameContainingIgnoreCase(String name);

    List<Product> findByNameContainingIgnoreCaseAndType(String name, String type);

}
