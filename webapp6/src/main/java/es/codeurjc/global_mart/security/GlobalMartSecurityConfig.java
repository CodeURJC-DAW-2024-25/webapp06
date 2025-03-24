package es.codeurjc.global_mart.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.authentication.AuthenticationManager;
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
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class)
                .authenticationProvider(authenticationProvider())
                .build();
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

        http.authenticationProvider(authenticationProvider()); // pasas el authProvider que has creado en la
                                                               // función
                                                               // anterior

        // Disable CSRF protection
        http.csrf(csrf -> csrf.disable());

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
                .requestMatchers("/products/allProducts").permitAll()
                .requestMatchers("/product/{id}").permitAll()
                .requestMatchers("/descriptionProduct").permitAll()
                .requestMatchers("/search").permitAll()
                .requestMatchers("/products/{type}").permitAll()
                .requestMatchers("/payCart").permitAll()
                .requestMatchers("/moreProdsAll").permitAll()
                .requestMatchers("/moreProdsTypes").permitAll()

                // -------------- PRIVATE PAGES ----------------
                .requestMatchers("/profile").authenticated()
                .requestMatchers("/shoppingcart").authenticated()
                .requestMatchers("/shoppingcart/*").authenticated()
                .requestMatchers("/new_product").hasRole("COMPANY")
                .requestMatchers("/displayGraphs").permitAll()
                // ----------------- ADMIN PAGES ----------------
                .requestMatchers("/adminPage").hasAnyRole("ADMIN")
                .requestMatchers("/profile").authenticated()
                .requestMatchers("/new_product").permitAll()
                .requestMatchers("/acceptProduct/{id}").hasAnyRole("ADMIN") // only admin can accept products
                .requestMatchers("/deleteProduct/{id}").hasAnyRole("ADMIN")
                .requestMatchers("/profile").permitAll()
                .requestMatchers("/showUserGraphic").permitAll()

                // -------------- API PAGES ----------------
                // MAIN CONTROLLER
                .requestMatchers("/api/main/mostViewedProducts").permitAll()
                .requestMatchers("/api/main/lastProducts").permitAll()
                .requestMatchers("/api/main/acceptedProducts").permitAll()
                .requestMatchers("/api/main/acceptedProductsByType/{type}").permitAll()
                .requestMatchers("/api/main/acceptedCompanyProducts").permitAll()
                .requestMatchers("/api/main/product/{id}").permitAll()
                .requestMatchers("/api/main/profile").authenticated()
                .requestMatchers("/api/main/moreProdsAll").permitAll()
                .requestMatchers("/api/main/moreProdsTypes/{type}").permitAll()
                .requestMatchers("/api/main/moreProdsCompany").hasRole("COMPANY")

                .requestMatchers("/api/auth/login").permitAll()
                .requestMatchers("/api/auth/refresh").permitAll()
                .requestMatchers("/api/auth/logout").permitAll()

                .anyRequest().permitAll()

        )
                // configure login and logout
                .formLogin(formLogin -> formLogin
                        .loginPage("/login")
                        .failureUrl("/login_error")
                        .successHandler((request, response, authentication) -> {
                            for (var authority : authentication.getAuthorities()) {
                                String role = authority.getAuthority();
                                if (role.equals("ROLE_ADMIN")) {
                                    response.sendRedirect("/adminPage");
                                    return;
                                } else if (role.equals("ROLE_COMPANY")) {
                                    response.sendRedirect("/new_product");
                                    return;
                                } else if (role.equals("ROLE_USER")) {
                                    response.sendRedirect("/");
                                    return;
                                }
                            }
                        })
                        .permitAll())
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/login")
                        .defaultSuccessUrl("/loginComprobation", true))

                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/")
                        .permitAll())

                // manage exceptions
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint((request, response, authException) -> response.sendRedirect("/")));

        return http.build();

    }

}
