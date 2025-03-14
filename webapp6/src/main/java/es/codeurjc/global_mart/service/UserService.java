package es.codeurjc.global_mart.service;

import es.codeurjc.global_mart.repository.UserRepository;
import es.codeurjc.global_mart.model.User;
import es.codeurjc.global_mart.model.Product;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import org.hibernate.engine.jdbc.BlobProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    public User createUser(MultipartFile image, String name, String username, String email, String password,
            List<String> role) throws IOException {
        User user = new User(name, username, email, password, role);
        if (image != null && !image.isEmpty()) {
            user.setImage(BlobProxy.generateProxy(image.getInputStream(), image.getSize()));
        } else {
            user.setImage(BlobProxy.generateProxy(
                    "https://www.pngitem.com/pimgs/m/146-1468479_my-profile-icon-blank-profile-picture-circle-hd.png"
                            .getBytes()));
        }
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
            user.setPassword(passwordEncoder.encode(password));
            return userRepository.save(user);
        } else {
            throw new RuntimeException("User not found with id " + id);
        }
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public void save(User user) {
        userRepository.save(user);
    }

    public List<Product> getCartProducts(User user) {
        return user.getCart();
    }

    public void addProductToCart(User user, Product product) {
        user.addProductToCart(product);
        userRepository.save(user);
    }

    public void removeProductFromCart(User user, Product product) {
        user.removeProductFromCart(product);
        userRepository.save(user);
    }

    public double getTotalPrice(User user) {
        return user.getCartPrice();
    }

    public void restartCart(User user) {
        user.emptyCart();
        userRepository.save(user);
    }

    public Optional<User> findByEmail(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        return user;
    }

    public List<Double> getLast15OrderPrices(String username) {
        Optional<User> user = userRepository.findByUsername(username);
        List<Double> ordersPrices = user.get().getHistoricalOrderPrices();
        int size = ordersPrices.size();
        return ordersPrices.subList(Math.max(size - 15, 0), size);
    }

}