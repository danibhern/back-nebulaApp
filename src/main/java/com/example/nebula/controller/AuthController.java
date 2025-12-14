package com.example.nebula.controller;

import com.example.nebula.dto.ApiResponse;
import com.example.nebula.dto.auth.UserLoginDto;
import com.example.nebula.dto.auth.UserRegisterDto;
import com.example.nebula.dto.auth.UserResponse;
import com.example.nebula.config.JwtUtil;
import com.example.nebula.model.User;
import com.example.nebula.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@CrossOrigin(origins = "*")
public class AuthController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    public AuthController(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserResponse>> register(@Valid @RequestBody UserRegisterDto registerDto) {
        try {
            // CAMBIO CLAVE: El Controller ya no crea el 'User'.
            // Delega TODA la lógica de registro al Service, pasándole el DTO.
            User savedUser = userService.registerUserFromDto(registerDto); // Llamamos a un nuevo método en el servicio

            // El resto del código para generar el token y la respuesta es perfecto.
            String token = jwtUtil.generateToken(savedUser.getEmail(), savedUser.getId());

            UserResponse response = new UserResponse(
                    savedUser.getId(),
                    savedUser.getName(),
                    savedUser.getEmail(),
                    token
            );

            return ResponseEntity.ok(ApiResponse.success("Usuario registrado exitosamente", response));

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<UserResponse>> login(@Valid @RequestBody UserLoginDto loginDto) {
        try {
            String token = userService.authenticateUser(loginDto.getEmail(), loginDto.getPassword());

            User user = userService.findByEmail(loginDto.getEmail())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            UserResponse response = new UserResponse(
                    user.getId(),
                    user.getName(),
                    user.getEmail(),
                    token
            );

            return ResponseEntity.ok(ApiResponse.success("Login exitoso", response));

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Credenciales inválidas"));
        }
    }
}