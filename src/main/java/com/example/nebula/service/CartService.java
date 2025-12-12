package com.example.nebula.service;

import com.example.nebula.model.Cart;
import com.example.nebula.model.CartItem;
import com.example.nebula.model.Product;
import com.example.nebula.model.User;
import com.example.nebula.repository.CartItemRepository;
import com.example.nebula.repository.CartRepository;
import com.example.nebula.repository.ProductRepository;
import com.example.nebula.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    public CartService(CartRepository cartRepository,
                       CartItemRepository cartItemRepository,
                       UserRepository userRepository,
                       ProductRepository productRepository) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
    }

    public Cart getOrCreateCartForUser(Long userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            throw new RuntimeException("Usuario no encontrado");
        }

        User user = userOptional.get();
        Optional<Cart> cartOptional = cartRepository.findByUser(user);

        if (cartOptional.isPresent()) {
            return cartOptional.get();
        } else {
            Cart newCart = new Cart();
            newCart.setUser(user);
            return cartRepository.save(newCart);
        }
    }

    public Cart getCartByUserEmail(String email) {
        Optional<Cart> cartOptional = cartRepository.findByUserEmail(email);
        if (cartOptional.isEmpty()) {
            Optional<User> userOptional = userRepository.findByEmail(email);
            if (userOptional.isEmpty()) {
                throw new RuntimeException("Usuario no encontrado");
            }
            return getOrCreateCartForUser(userOptional.get().getId());
        }
        return cartOptional.get();
    }

    @Transactional
    public Cart addProductToCart(Long userId, Long productId, Integer quantity) {
        Cart cart = getOrCreateCartForUser(userId);
        Optional<Product> productOptional = productRepository.findById(productId);

        if (productOptional.isEmpty()) {
            throw new RuntimeException("Producto no encontrado");
        }

        Product product = productOptional.get();
        Optional<CartItem> existingItem = cartItemRepository.findByCartAndProduct(cart, product);

        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + quantity);
            cartItemRepository.save(item);
        } else {
            CartItem newItem = new CartItem();
            newItem.setCart(cart);
            newItem.setProduct(product);
            newItem.setQuantity(quantity);
            cartItemRepository.save(newItem);
        }

        return cartRepository.save(cart);
    }

    @Transactional
    public Cart removeProductFromCart(Long userId, Long productId, Integer quantity) {
        Cart cart = getOrCreateCartForUser(userId);
        Optional<Product> productOptional = productRepository.findById(productId);

        if (productOptional.isEmpty()) {
            throw new RuntimeException("Producto no encontrado");
        }

        Product product = productOptional.get();
        Optional<CartItem> existingItem = cartItemRepository.findByCartAndProduct(cart, product);

        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            int newQuantity = item.getQuantity() - quantity;

            if (newQuantity <= 0) {
                cartItemRepository.delete(item);
            } else {
                item.setQuantity(newQuantity);
                cartItemRepository.save(item);
            }
        }

        return cartRepository.save(cart);
    }

    @Transactional
    public void deleteItemFromCart(Long userId, Long itemId) {
        Cart cart = getOrCreateCartForUser(userId);
        Optional<CartItem> itemOptional = cartItemRepository.findById(itemId);

        if (itemOptional.isPresent() && itemOptional.get().getCart().getId().equals(cart.getId())) {
            cartItemRepository.deleteById(itemId);
        } else {
            throw new RuntimeException("Ítem no encontrado en el carrito");
        }
    }
}