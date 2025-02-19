package es.codeurjc.global_mart.service;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import es.codeurjc.global_mart.repository.CartRepository;
import es.codeurjc.global_mart.model.Cart;
import es.codeurjc.global_mart.model.Product;
import es.codeurjc.global_mart.repository.ProductRepository;

@Service
public class CartService {
    
    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ProductRepository productRepository;

    public void createCart() {                      //  if the costumer dont have a cart yet
        Cart cart = new Cart();
        cartRepository.save(cart);
    }


    public Cart addProductToCart(Long userId, Long productId){          // if the customer also haves a cart

        Cart cart = cartRepository.findByUserId(userId).orElseThrow(() -> new RuntimeException("Cart not found for user id: " + userId));
        Product product = productRepository.findById(productId).orElseThrow(() -> new RuntimeException("Product not found for id: " + productId));
        cart.addProduct(product);
        cartRepository.save(cart);
        return cart;
    }

    public Cart deleteProductFromCart(Long userId, Long productId){          // if the customer also haves a cart

        Cart cart = cartRepository.findByUserId(userId).orElseThrow(() -> new RuntimeException("Cart not found for user id: " + userId));
        Product product = productRepository.findById(productId).orElseThrow(() -> new RuntimeException("Product not found for id: " + productId));
        cart.deleteProduct(product);
        cartRepository.save(cart);
        return cart;
    }

    public Cart getCart(Long userId) {
        return cartRepository.findByUserId(userId).orElseThrow(() -> new RuntimeException("Cart not found for user id: " + userId));
    }

}
