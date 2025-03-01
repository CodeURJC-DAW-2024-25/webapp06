package es.codeurjc.global_mart.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;

@Configuration
@EnableWebSecurity
public class GlobalMartSecurityConfig {

    @Autowired
    RepositoryUserDetailsService userDetailsService;

    // encode user password
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();

        authenticationProvider.setUserDetailsService(userDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEncoder());

        return authenticationProvider;

    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception { // configura las paginas
                                                                                         // publicas y privadas

        http.authenticationProvider(authenticationProvider()); // pasas el authProvider que has creado en la función
                                                               // anterior
        http.authorizeHttpRequests(authorize -> authorize
                // PUBLIC PAGES
                .anyRequest().permitAll()
                )
                // .requestMatchers("/").permitAll()
                // .requestMatchers("/about").permitAll()
                // .requestMatchers("/choose_login_option").permitAll()
                // .requestMatchers("/register").permitAll()
                // .requestMatchers("/login").permitAll()
                // .requestMatchers("/products/{type}").permitAll()
                // .requestMatchers("/product/{id}").permitAll()
                // .requestMatchers("/descriptionProduct").permitAll()
                // .requestMatchers("/search").permitAll()
                // .requestMatchers("/error").permitAll()

                // Configurar el formulario de login
                .formLogin(formLogin -> formLogin
                        .loginProcessingUrl("/")
                        .failureUrl("/login_error")
                        .defaultSuccessUrl("/")
                        .permitAll())

                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/")
                        .permitAll())

                // Configurar manejo de excepciones para redirigir a la página de login si no se
                // está autenticado
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(
                                (request, response, authException) -> response.sendRedirect("/login")));

        return http.build();

    }

}
