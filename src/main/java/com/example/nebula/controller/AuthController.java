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
            // 1. Crear usuario
            User user = new User();
            user.setName(registerDto.getName());
            user.setEmail(registerDto.getEmail());
            user.setPassword(registerDto.getPassword());

            // 2. Registrar (hashea password)
            User savedUser = userService.registerUser(user);

            // 3. Generar token JWT
            String token = jwtUtil.generateToken(savedUser.getEmail(), savedUser.getId());

            // 4. Crear respuesta
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
            // 1. Autenticar (verifica email/password y genera token)
            String token = userService.authenticateUser(loginDto.getEmail(), loginDto.getPassword());

            // 2. Obtener usuario
            User user = userService.findByEmail(loginDto.getEmail())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            // 3. Crear respuesta
            UserResponse response = new UserResponse(
                    user.getId(),
                    user.getName(),
                    user.getEmail(),
                    token
            );

            return ResponseEntity.ok(ApiResponse.success("Login exitoso", response));

        } catch (RuntimeException e) {
            // Mensaje genérico por seguridad
            return ResponseEntity.badRequest().body(ApiResponse.error("Credenciales inválidas"));
        }
    }
}