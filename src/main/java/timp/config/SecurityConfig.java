package timp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.http.HttpStatus;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    public SecurityConfig() {
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
                )
                .headers(headers -> headers
                        .frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin)
                )
                .formLogin(form -> form
                        .loginProcessingUrl("/api/auth/login")
                        .successHandler((req, resp, auth) -> {
                            resp.setStatus(200);
                            resp.setContentType("application/json");
                            resp.setCharacterEncoding("UTF-8");
                            resp.getWriter().write(
                                    String.format("{\"username\":\"%s\",\"success\":true}",
                                            auth.getName())
                            );
                        })
                        .failureHandler((req, resp, e) -> {
                            resp.setStatus(401);
                            resp.setContentType("application/json");
                            resp.setCharacterEncoding("UTF-8");
                            resp.getWriter().write("{\"message\":\"Неверный логин или пароль\",\"success\":false}");
                        })
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/api/auth/logout")
                        .logoutSuccessHandler((req, resp, auth) -> resp.setStatus(200))
                )
                .authorizeHttpRequests(auth -> auth
                        // Allow H2 console
                        .requestMatchers("/h2-console/**").permitAll()
                        // Allow turnstile (no auth required)
                        .requestMatchers("/api/turnstile/**").permitAll()
                        // Allow sensors (for IoT devices)
                        .requestMatchers("/api/sensors/**").permitAll()
                        // All other API endpoints require authentication
                        .requestMatchers("/api/**").authenticated()
                );

        return http.build();
    }
}
