package com.example.nebula.controller;

import com.example.nebula.dto.ApiResponse;
import com.example.nebula.dto.cart.AddToCartRequest;
import com.example.nebula.dto.cart.CartItemResponse;
import com.example.nebula.dto.cart.CartResponse;
import com.example.nebula.model.Cart;
import com.example.nebula.model.CartItem;
import com.example.nebula.model.User;
import com.example.nebula.service.CartService;
import com.example.nebula.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/cart")
@CrossOrigin(origins = "*")
public class CartController {

    private final CartService cartService;
    private final UserService userService;

    public CartController(CartService cartService, UserService userService) {
        this.cartService = cartService;
        this.userService = userService;
    }
    private User getAuthenticatedUser(Authentication authentication) {
        String userEmail = authentication.getName();
        return userService.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con el email: " + userEmail));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<CartResponse>> getMyCart(Authentication authentication) {
        try {
            User user = getAuthenticatedUser(authentication);
            Cart cart = cartService.getOrCreateCartForUser(user.getId());
            CartResponse response = convertToCartResponse(cart);
            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/add")
    public ResponseEntity<ApiResponse<CartResponse>> addProductToCart(
            @Valid @RequestBody AddToCartRequest request,
            Authentication authentication) { // <-- También se inyecta aquí
        try {
            User user = getAuthenticatedUser(authentication);
            Long productId = Long.parseLong(request.getProductId());
            Cart cart = cartService.addProductToCart(user.getId(), productId, request.getQuantity());
            CartResponse response = convertToCartResponse(cart);
            return ResponseEntity.ok(ApiResponse.success("Producto añadido al carrito", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("No se pudo añadir el producto: " + e.getMessage()));
        }
    }

    @PatchMapping("/remove")
    public ResponseEntity<ApiResponse<CartResponse>> removeProductFromCart(
            @RequestParam Long productId,
            @RequestParam(defaultValue = "1") Integer quantity,
            Authentication authentication) {
        try {
            User user = getAuthenticatedUser(authentication);
            Cart cart = cartService.removeProductFromCart(user.getId(), productId, quantity);
            CartResponse response = convertToCartResponse(cart);
            return ResponseEntity.ok(ApiResponse.success("Producto removido del carrito", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @DeleteMapping("/item/{itemId}")
    public ResponseEntity<ApiResponse<Void>> deleteItemFromCart(
            @PathVariable Long itemId,
            Authentication authentication) {
        try {
            User user = getAuthenticatedUser(authentication);
            cartService.deleteItemFromCart(user.getId(), itemId);
            return ResponseEntity.ok(ApiResponse.success("Ítem eliminado del carrito", null));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    // --- Los métodos de conversión se mantienen igual ---
    private CartResponse convertToCartResponse(Cart cart) {
        List<CartItemResponse> items = cart.getItems().stream()
                .map(this::convertToCartItemResponse)
                .collect(Collectors.toList());
        return new CartResponse(cart.getId(), items, cart.getSubtotal(), cart.getTotal());
    }

    private CartItemResponse convertToCartItemResponse(CartItem item) {
        return new CartItemResponse(
                item.getId(),
                item.getProduct().getId().toString(),
                item.getProduct().getName(),
                item.getProduct().getPrice(),
                item.getQuantity(),
                item.getProduct().getImageUrl()
        );
    }
}
