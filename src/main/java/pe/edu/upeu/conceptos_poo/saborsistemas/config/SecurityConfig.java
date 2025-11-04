package pe.edu.upeu.conceptos_poo.saborsistemas.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        // Usa BCrypt, el estándar moderno para hashing de contraseñas
        return new BCryptPasswordEncoder();
    }
}
