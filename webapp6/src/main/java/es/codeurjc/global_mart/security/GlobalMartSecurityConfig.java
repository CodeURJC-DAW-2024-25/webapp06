package es.codeurjc.global_mart.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;

@Configuration
@EnableWebSecurity
public class GlobalMartSecurityConfig {

        @Autowired
        public RepositoryUserDetailsService userDetailsService;

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

                http.authenticationProvider(authenticationProvider()); // pasas el authProvider que has creado en la
                                                                       // función
                                                                       // anterior
                http.authorizeHttpRequests(authorize -> authorize

                                // -------------- STYLE PAGES ----------------
                                .requestMatchers("/css/**").permitAll()
                                .requestMatchers("/js/**").permitAll()
                                .requestMatchers("/images/**").permitAll()
                                // -------------- PUBLIC PAGES ----------------
                                .requestMatchers("/").permitAll()
                                .requestMatchers("/about").permitAll()
                                .requestMatchers("/choose_login_option").permitAll()
                                .requestMatchers("/register").permitAll()
                                .requestMatchers("/login").permitAll()
                                .requestMatchers("/allProducts").permitAll()
                                .requestMatchers("/product/{id}").permitAll()
                                .requestMatchers("/descriptionProduct").permitAll()
                                .requestMatchers("/search").permitAll()
                                .requestMatchers("/{type}").permitAll()
                                //----------------- PRIVATE PAGES ----------------
                                //----------------- ADMIN PAGES ----------------
                                .requestMatchers("/adminPage").hasAnyRole("ADMIN")
                                .requestMatchers("/profile").authenticated()
                                // .requestMatchers("/shoppingcart").permitAll()
                                // .requestMatchers("/error").permitAll()

                                

                                // -------------- PRIVATE PAGES ----------------
                                .requestMatchers("/shoppingcart").authenticated())

                                // Configurar el formulario de login
                                .formLogin(formLogin -> formLogin
                                                .loginPage("/login")
                                                .failureUrl("/login_error")
                                                .successHandler((request, response, authentication) -> {
                                                        authentication.getAuthorities().forEach(grantedAuthority -> {
                                                                try {
                                                                        if (grantedAuthority.getAuthority()
                                                                                        .equals("ROLE_ADMIN")) {
                                                                                response.sendRedirect("/adminPage");
                                                                        } else if (grantedAuthority.getAuthority()
                                                                                        .equals("ROLE_COMPANY")) {
                                                                                response.sendRedirect("/new_product");
                                                                        } else if (grantedAuthority.getAuthority()
                                                                                        .equals("ROLE_USER")) {
                                                                                response.sendRedirect("/");
                                                                        }
                                                                } catch (Exception e) {
                                                                        e.printStackTrace();
                                                                }
                                                        });
                                                })
                                                .permitAll())

                                .logout(logout -> logout
                                                .logoutUrl("/logout")
                                                .logoutSuccessUrl("/")
                                                .permitAll())

                                // Configurar manejo de excepciones para redirigir a la página de login si no se
                                // está autenticado
                                .exceptionHandling(exception -> exception
                                                .authenticationEntryPoint(
                                                                (request, response, authException) -> response
                                                                                .sendRedirect("/")));

                return http.build();

        }

}
