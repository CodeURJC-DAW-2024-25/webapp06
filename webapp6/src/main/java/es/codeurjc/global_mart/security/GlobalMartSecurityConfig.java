package es.codeurjc.global_mart.security;

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

    // encode user password
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Autowired
    private JwtRequestFilter jwtRequestFilter;

    @Autowired
    private UnauthorizedHandlerJwt unauthorizedHandlerJwt;

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
    @Order(1)
    public SecurityFilterChain apiFilterChain(HttpSecurity http) throws Exception {

        http.authenticationProvider(authenticationProvider());

        http.securityMatcher("/v1/sapi/**")
                .exceptionHandling(handling -> handling.authenticationEntryPoint(unauthorizedHandlerJwt));

        http.authorizeHttpRequests(authorize -> authorize
                // MainAPI

                .requestMatchers(HttpMethod.GET, "/v1/api/main/profile").authenticated()

                // ProductsAPI
                // Image
                .requestMatchers(HttpMethod.GET, "/v1/api/products/{id}/image").permitAll()
                .requestMatchers(HttpMethod.POST, "/v1/api/products/{id}/image").permitAll()
                .requestMatchers(HttpMethod.DELETE, "/v1/api/products/{id}/image").permitAll()
                .requestMatchers(HttpMethod.PUT, "/v1/api/products/{id}/image").permitAll()
                // Product
                
                .requestMatchers(HttpMethod.GET, "/v1/api/products/").permitAll()   //Este comando tiene mucha informacion como parametros que hace que pueda ser diferentes tipos de comando (observar info)
                .requestMatchers(HttpMethod.GET, "/v1/api/products/{id}").permitAll()
                .requestMatchers(HttpMethod.GET, "/v1/api/products/type").permitAll()
                .requestMatchers(HttpMethod.POST, "/v1/api/products/").hasRole("COMPANY")
                .requestMatchers(HttpMethod.PUT, "/v1/api/products/{id}").hasAnyRole("COMPANY", "ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/v1/api/products/{id}").hasAnyRole("COMPANY", "ADMIN")
                // Algorithm
                .requestMatchers(HttpMethod.GET, "/v1/api/products/mostViewedProducts").permitAll()
                .requestMatchers(HttpMethod.GET, "/v1/api/products/lastProducts").permitAll()
                // Page --> se hace introduciendo page= x a traves de la funciona de v1/api/products/

                // ReviewsAPI
                .requestMatchers(HttpMethod.POST, "/v1/api/reviews/{id}").authenticated()
                .requestMatchers(HttpMethod.GET, "/v1/api/reviews/{id}").permitAll()
                .requestMatchers(HttpMethod.GET, "/v1/api/reviews").permitAll()

                // ShoppingCartAPI
                .requestMatchers(HttpMethod.GET, "/v1/api/shoppingCarts/").hasRole("USER")
                .requestMatchers(HttpMethod.DELETE, "/v1/api/shoppingCarts/{id}").hasRole("USER")
                .requestMatchers(HttpMethod.POST, "/v1/api/shoppingCarts/{id}").hasRole("USER")
                .requestMatchers(HttpMethod.POST, "/v1/api/shoppingCarts/payment").hasRole("USER")

                // UserAPI
                .requestMatchers(HttpMethod.PUT, "/v1/api/users/").permitAll()
                .requestMatchers(HttpMethod.DELETE, "/v1/api/users/{id}").permitAll()
                .requestMatchers(HttpMethod.POST, "/v1/api/users/").permitAll()
                .requestMatchers(HttpMethod.GET, "/v1/api/users/{id}").permitAll()
                .requestMatchers(HttpMethod.GET, "/v1/api/users/").permitAll()
                // Image
                .requestMatchers(HttpMethod.GET, "/v1/api/users/{id}/image").permitAll()
                .requestMatchers(HttpMethod.POST, "/v1/api/users/{id}/image").permitAll()
                .requestMatchers(HttpMethod.DELETE, "/v1/api/users/{id}/image").permitAll()
                .requestMatchers(HttpMethod.PUT, "/v1/api/users/{id}/image").permitAll()

                .anyRequest().permitAll());

        http.formLogin(formLogin -> formLogin.disable());
        http.csrf(csrf -> csrf.disable());
        http.httpBasic(httpBasic -> httpBasic.disable());

        http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();

    }

    @Bean
    @Order(2)
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
