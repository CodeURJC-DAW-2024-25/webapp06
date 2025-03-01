package es.codeurjc.global_mart.repository;

import es.codeurjc.global_mart.model.User;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username); // Busca un usuario por nombre de usuario
    
}
