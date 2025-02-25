package es.codeurjc.global_mart.service;

import es.codeurjc.global_mart.repository.UserRepository;
import es.codeurjc.global_mart.model.User;

import org.springframework.stereotype.Service;

import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public User createUser(String image, String name, String username, String email, String password,
            List<String> role) {
        User user = new User(image, name, username, email, password, role);
        return userRepository.save(user);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public User updateUser(Long id, String username, String email, String password) {
        Optional<User> optionalUser = getUserById(id);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setUsername(username);
            user.setEmail(email);
            user.setPassword(password);
            return userRepository.save(user);
        } else {
            throw new RuntimeException("User not found with id " + id);
        }
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public Optional<User> login(String usernameOrEmail, String password) {
        Optional<User> userOpt = userRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail); // Busca el
                                                                                                         // ussuario por
                                                                                                         // nombre de
                                                                                                         // usuario o
                                                                                                         // email
        System.out.println("Login recibido - username/email: " + usernameOrEmail + ", password: " + password);
        if (userOpt.isPresent()) { // Si el usuario existe
            User user = userOpt.get(); // Obtiene el usuario
            System.out.println("Login correcto: " + user.getUsername() + " - " + user.getEmail());
            // Se asume que la contraseña se almacena en texto plano
            if (user.getPassword().equals(password)) { // Si la contraseña es correcta
                return Optional.of(user); // Devuelve el usuario
            }
        }
        return Optional.empty();
    }
}