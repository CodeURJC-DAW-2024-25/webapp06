package es.codeurjc.global_mart.service;

import es.codeurjc.global_mart.repository.UserRepository;
import es.codeurjc.global_mart.model.User;
import es.codeurjc.global_mart.dto.Product.ProductDTO;
import es.codeurjc.global_mart.dto.Product.ProductMapper;
import es.codeurjc.global_mart.dto.User.UserDTO;
import es.codeurjc.global_mart.dto.User.HistoricalOrdersDTO;
import es.codeurjc.global_mart.dto.User.ShoppingCartDTO;
import es.codeurjc.global_mart.dto.User.UserCartPriceDTO;
import es.codeurjc.global_mart.dto.User.UserMapper;
import es.codeurjc.global_mart.model.Product;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import org.hibernate.engine.jdbc.BlobProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.io.IOException;
import java.sql.Blob;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ProductMapper productMapper;

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

    public List<UserDTO> getAllUsers() {
        return userMapper.toUsersDTO(userRepository.findAll());
    }

    public Optional<UserDTO> getUserById(Long id) {
        return userRepository.findById(id).map(userMapper::toUserDTO); // transform an optional user into an optional
                                                                       // userdto
    }

    public UserDTO updateUser(Long id, String username, String email, String password) {
        Optional<UserDTO> optionalUser = getUserById(id);
        if (optionalUser.isPresent()) {
            User user = userMapper.toUser(optionalUser.get()); // from DTO tu entity
            user.setUsername(username);
            user.setEmail(email);
            user.setPassword(passwordEncoder.encode(password));
            userRepository.save(user);
            return userMapper.toUserDTO(user);
        } else {
            throw new RuntimeException("User not found with id " + id);
        }
    }

    public Optional<UserDTO> findByUsername(String username) {
        return userRepository.findByUsername(username).map(userMapper::toUserDTO);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public void save(User user) {
        userRepository.save(user);
    }

    public ShoppingCartDTO getShoppingCartData(UserDTO user) {
        User u = userMapper.toUser(user);
        List<ProductDTO> cartProducts = productMapper.toProductsDTO(u.getCart()); // return a a productsDTO list
        Double price = u.getCartPrice();
        return new ShoppingCartDTO(cartProducts, price);
    }

    // public void addImageDataToProducts(List<ProductDTO> products) {
    // for (ProductDTO productDTO : products) {
    // try {
    // Product product = productMapper.toProduct(productDTO);
    // Blob imageBlob = product.getImage();
    // if (imageBlob != null) {
    // byte[] bytes = imageBlob.getBytes(1, (int) imageBlob.length());
    // String imageBase64 = "data:image/jpeg;base64," +
    // Base64.getEncoder().encodeToString(bytes);

    // // necesitamos crear un nuevo DTO con la imagen en base64 para actualizarlo,
    // ya
    // // que asignando otra vez lo unico que hacemos es actualizar la variable
    // local
    // // de este codigo
    // var updatedProduct = new ProductDTO(
    // productDTO.id(),
    // productDTO.type(),
    // productDTO.name(),
    // productDTO.company(),
    // productDTO.price(),
    // productDTO.description(),
    // productDTO.image(),
    // productDTO.stock(),
    // productDTO.isAccepted(),
    // productDTO.date(),
    // productDTO.views_count(),
    // productDTO.reviews(),
    // imageBase64 // Set the imageBase64 field
    // );

    // int index = products.indexOf(productDTO);
    // if (index != -1) {
    // products.set(index, updatedProduct);
    // }
    // }
    // } catch (Exception e) {
    // e.printStackTrace();
    // }
    // }
    // }

    public void addProductToCart(UserDTO userDTO, ProductDTO productDTO) {
        User user = userMapper.toUser(userDTO);
        Product product = productMapper.toProduct(productDTO);
        user.addProductToCart(product);
        userRepository.save(user);
    }

    public boolean productInCart(UserDTO userDTO, ProductDTO productDTO) {
        User user = userMapper.toUser(userDTO);
        Product product = productMapper.toProduct(productDTO);

        return user.getCart().contains(product);

    }

    public void removeProductFromCart(UserDTO userDTO, ProductDTO productDTO) {
        User user = userMapper.toUser(userDTO);
        Product product = productMapper.toProduct(productDTO);
        user.removeProductFromCart(product);
        userRepository.save(user);
    }

    public UserCartPriceDTO getTotalPrice(User user) {
        return userMapper.toCartPriceDTO(user);
    }

    public void restartCart(User user) {
        user.emptyCart();
        userRepository.save(user);
    }

    public Optional<UserDTO> findByEmail(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        return user.map(userMapper::toUserDTO);
    }

    public HistoricalOrdersDTO getUserStads(String name) {
        User user = userRepository.findByUsername(name)
                .orElseThrow(() -> new RuntimeException("User not found with username " + name));

        return new HistoricalOrdersDTO(user.getHistoricalOrderPrices());
    }

    // DUDA CON ESTOS TRES METODOS: es correcto transformar el DTO en un User para
    // comprobar si es admin, company o user?
    // o debería de comprobar la lista del DTO?
    public boolean isAdmin(UserDTO userDTO) {
        User user = userMapper.toUser(userDTO);
        return user.isAdmin();
    }

    public boolean isCompany(UserDTO userDTO) {
        User user = userMapper.toUser(userDTO);
        return user.isCompany();
    }

    public boolean isUser(UserDTO userDTO) {
        User user = userMapper.toUser(userDTO);
        return user.isUser();
    }

    public void convertBlobToBase64(ProductDTO productDTO) {
        Product product = productMapper.toProduct(productDTO);
        // convert Blob to Base64 encoded string
        try {
            Blob imageBlob = product.getImage();
            if (imageBlob != null) {
                byte[] bytes = imageBlob.getBytes(1, (int) imageBlob.length());
                String imageBase64 = "data:image/jpeg;base64," + Base64.getEncoder().encodeToString(bytes);
                product.setImageBase64(imageBase64);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}