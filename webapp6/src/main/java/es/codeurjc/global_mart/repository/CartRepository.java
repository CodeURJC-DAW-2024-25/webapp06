package es.codeurjc.global_mart.repository;
import es.codeurjc.global_mart.model.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {
    Optional<Cart> findByUserId(Long userId);                   // Find the cart of a user       
}
