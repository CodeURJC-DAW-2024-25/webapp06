package es.codeurjc.global_mart.repository;

import es.codeurjc.global_mart.model.User;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsernameOrEmail(String username, String email); // Busca un usuario por nombre de usuario o
                                                                         // email
}
