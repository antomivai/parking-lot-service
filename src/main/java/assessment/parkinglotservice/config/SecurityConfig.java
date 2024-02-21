package assessment.parkinglotservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
public class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .oauth2ResourceServer((oauth2) -> oauth2  // authentication
                        .jwt(withDefaults())
                )
                .authorizeHttpRequests((authorize) -> authorize  // authorization
                        .requestMatchers(HttpMethod.GET, "/parkinglot/**").permitAll()
                        .anyRequest().authenticated()
                );

        return http.build();
    }
}
