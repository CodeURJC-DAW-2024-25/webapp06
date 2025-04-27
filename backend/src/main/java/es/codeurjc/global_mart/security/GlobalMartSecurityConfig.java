package es.codeurjc.global_mart.security;

import java.util.Arrays;
import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import es.codeurjc.global_mart.security.jwt.JwtRequestFilter;
import es.codeurjc.global_mart.security.jwt.UnauthorizedHandlerJwt;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;

@Configuration
@EnableWebSecurity
public class GlobalMartSecurityConfig {

    @Autowired
    public RepositoryUserDetailsService userDetailsService;

    @Autowired
    private JwtRequestFilter jwtRequestFilter;

    @Autowired
    private UnauthorizedHandlerJwt unauthorizedHandlerJwt;

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
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // Permitir peticiones desde cualquier origen durante desarrollo
        configuration.addAllowedOrigin("http://localhost:4200");

        // Permitir todos los métodos HTTP comunes
        configuration.addAllowedMethod("GET");
        configuration.addAllowedMethod("POST");
        configuration.addAllowedMethod("PUT");
        configuration.addAllowedMethod("DELETE");
        configuration.addAllowedMethod("OPTIONS");
        configuration.addAllowedMethod("PATCH");
        configuration.addAllowedMethod("HEAD");

        // Permitir todos los headers comunes
        configuration.addAllowedHeader("*");

        // Exponer headers específicos que el cliente podría necesitar leer
        configuration.addExposedHeader("Authorization");
        configuration.addExposedHeader("Content-Disposition");

        // Permitir credenciales (cookies, auth headers, etc)
        configuration.setAllowCredentials(true);

        // Cache de preflight por 1 hora
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    @Order(1)
    public SecurityFilterChain apiFilterChain(HttpSecurity http) throws Exception {
        http.securityMatcher("/api/**");

        // Configuración de CORS y CSRF
        http.cors().configurationSource(corsConfigurationSource())
                .and()
                .csrf().disable();

        // Manejo de excepciones
        http.exceptionHandling()
                .authenticationEntryPoint(unauthorizedHandlerJwt);

        // Política de sesión
        http.sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        // Añadir filtro JWT
        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

        // Configuración de autenticación
        http.authenticationProvider(authenticationProvider());

        // Desactivar login de formulario y basic auth
        http.formLogin().disable();
        http.httpBasic().disable();

        // Configuración de autorizaciones
        http.authorizeHttpRequests(authorize -> authorize
                // Permitir explícitamente endpoints de autenticación
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/new/**").permitAll()
                // MainAPI
                .requestMatchers(HttpMethod.GET, "/api/main/profile").authenticated()

                // ProductsAPI
                // Image
                .requestMatchers(HttpMethod.GET, "api/products/{id}/image").permitAll()
                .requestMatchers(HttpMethod.POST, "api/products/{id}/image").permitAll()
                .requestMatchers(HttpMethod.DELETE, "api/products/{id}/image").permitAll()
                .requestMatchers(HttpMethod.PUT, "api/products/{id}/image").permitAll()
                // Product
                .requestMatchers(HttpMethod.GET, "/api/products/notAcceptedProducts").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/products/accept").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/products/delete").hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/products/{id}").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/products").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/products/type/{type}").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/products/").hasRole("COMPANY")
                .requestMatchers(HttpMethod.PUT, "/api/products/{id}").hasAnyRole("COMPANY", "ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/products/{id}").hasAnyRole("COMPANY", "ADMIN")
                // Algorithm
                .requestMatchers(HttpMethod.GET, "/api/products/mostViewedProducts").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/products/lastProducts").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/products/acceptedProducts").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/products/acceptedProductsByType/{type}").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/products/acceptedCompanyProducts").hasRole("COMPANY")
                // Page
                .requestMatchers(HttpMethod.GET, "/api/products/moreProdsAll").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/products/moreProdsType/{type}").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/product/moreProdsCompany").hasRole("COMPANY")

                // ReviewsAPI
                .requestMatchers(HttpMethod.POST, "/api/reviews/{id}").authenticated()
                .requestMatchers(HttpMethod.GET, "/api/reviews/{id}").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/reviews").permitAll()

                // ShoppingCartAPI
                .requestMatchers(HttpMethod.GET, "/api/shoppingCarts/").hasRole("USER")
                .requestMatchers(HttpMethod.DELETE, "/api/shoppingCarts/{id}").hasRole("USER")
                .requestMatchers(HttpMethod.POST, "/api/shoppingCarts/{id}").hasRole("USER")
                .requestMatchers(HttpMethod.POST, "/api/shoppingCarts/payment").hasRole("USER")

                // UserAPI
                .requestMatchers(HttpMethod.PUT, "/api/users/").permitAll()
                .requestMatchers(HttpMethod.DELETE, "/api/users/{id}").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/users/").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/users/{id}").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/users/").permitAll()
                // Image
                .requestMatchers(HttpMethod.GET, "/api/users/{id}/image").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/users/{id}/image").permitAll()
                .requestMatchers(HttpMethod.DELETE, "/api/users/{id}/image").permitAll()
                .requestMatchers(HttpMethod.PUT, "/api/users/{id}/image").permitAll()

                .anyRequest().denyAll());

        return http.build();
    }

    @Bean
    @Order(2)
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // Configuración CORS y CSRF
        http.cors().configurationSource(corsConfigurationSource())
                .and()
                .csrf().disable();

        // Configuración de autenticación
        http.authenticationProvider(authenticationProvider());

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

                .anyRequest().permitAll())

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