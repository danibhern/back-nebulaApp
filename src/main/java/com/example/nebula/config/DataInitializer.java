package com.example.nebula.config;

import com.example.nebula.model.Product;
import com.example.nebula.model.User;
import com.example.nebula.repository.ProductRepository;
import com.example.nebula.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initData(UserRepository userRepository,
                                      ProductRepository productRepository,
                                      PasswordEncoder passwordEncoder) {
        return args -> {
            // Crear usuario de prueba
            if (userRepository.findByEmail("admin@nebula.com").isEmpty()) {
                User admin = new User();
                admin.setName("Administrador");
                admin.setEmail("admin@nebula.com");
                admin.setPassword(passwordEncoder.encode("admin123"));
                userRepository.save(admin);

                User testUser = new User();
                testUser.setName("Usuario Test");
                testUser.setEmail("test@nebula.com");
                testUser.setPassword(passwordEncoder.encode("test123"));
                userRepository.save(testUser);
            }

            // Crear productos de prueba
            if (productRepository.count() == 0) {
                Product[] products = {
                        createProduct("Café Premium Daroma", "Café en grano 250g", 5890, "https://focusmood.coffee/cl/wp-content/uploads/2024/08/Envase-Arabico-Negro-1-600x450.png", "Café"),
                        createProduct("Manuel Café", "Café molido 500g", 10000, "https://manuelcaffe.cl/wp-content/uploads/2025/01/DOIPACK-Armonia_A_62e3ceb6-e998-47a6-b1ca-77804ae85a4e.png", "Café"),
                        createProduct("Café Nestle Bonka", "Café en grano 250g", 10000, "https://comercialboncafe.com/wp-content/uploads/2025/03/3D-BONKA_ECONOCMY_delizzia.png", "Café"),
                        createProduct("Kit Básico Barista", "6 unidades acero inoxidable", 19990, "https://berlingo.com.mx/wp-content/uploads/2024/12/KIT-BARISTA-3-600x600.png", "Insumos"),
                        createProduct("Jarra de 20 Oz", "Jarra acero inoxidable", 4990, "https://berlingo.com.mx/wp-content/uploads/2024/12/KIT-BARISTA-5-600x600.png", "Insumos"),
                        createProduct("Taza de Cerámica Nebula", "Taza premium con diseño exclusivo", 8990, "https://sensorial.cl/wp-content/uploads/2021/09/taza-grande.png", "Insumos"),
                        createProduct("Café Lavazza", "250g Molido", 10000, "https://multicoffee.es/wp-content/uploads/moido_lavazza_espresso.png", "Café"),
                        createProduct("Molinillo eléctrico", "No incluye baterias y/o pilas", 4500, "https://www.copacabana.com.uy/images/thumbs/0002111_molinillo-de-cafe-electrico_600.png", "Insumos")
                };

                productRepository.saveAll(Arrays.asList(products));
            }
        };
    }

    private Product createProduct(String name, String description, Integer price, String imageUrl, String category) {
        Product product = new Product();
        product.setName(name);
        product.setDescription(description);
        product.setPrice(price);
        product.setImageUrl(imageUrl);
        product.setCategory(category);
        return product;
    }
}