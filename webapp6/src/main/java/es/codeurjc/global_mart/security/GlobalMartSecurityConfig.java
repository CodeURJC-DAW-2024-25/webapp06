package es.codeurjc.global_mart.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.client.HttpClientErrorException.Unauthorized;
import es.codeurjc.global_mart.dto.Reviewss.ReviewMapperImpl;
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
    public SecurityFilterChain apiFilterChain(HttpSecurity http) throws Exception{

            http.authenticationProvider(authenticationProvider());

            http.securityMatcher("/api/**")
                .exceptionHandling(handling -> handling.authenticationEntryPoint(unauthorizedHandlerJwt));
            
            http.authorizeHttpRequests(authorize -> authorize
                //MainAPI
                .requestMatchers(HttpMethod.GET, "/api/main/mostViewedProducts").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/main/lastProducts").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/main/acceptedProducts").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/main/acceptedProductsByType/{type}").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/main/acceptedProductsByCompany").hasRole("COMPANY")
                .requestMatchers(HttpMethod.GET, "/api/main/profile").authenticated()
                .requestMatchers(HttpMethod.GET, "/api/main/moreProdsAll").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/main/moreProdsType/{type}").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/main/moreProdsCompany").hasRole("COMPANY")
                //ProductsAPI
                    //Image
                .requestMatchers(HttpMethod.GET, "api/products/{id}/image").permitAll()
                .requestMatchers(HttpMethod.POST, "api/products/{id}/image").permitAll()
                .requestMatchers(HttpMethod.DELETE, "api/products/{id}/image").permitAll()
                .requestMatchers(HttpMethod.PUT, "api/products/{id}/image").permitAll()
                    //Product
                .requestMatchers(HttpMethod.GET, "/api/products/{id}").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/products").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/products/type/{type}").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/products/").hasRole("COMPANY")
                .requestMatchers(HttpMethod.PUT, "/api/products/{id}").hasAnyRole("COMPANY", "ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/products/{id}").hasAnyRole("COMPANY", "ADMIN")
                
                //ReviewsAPI
                .requestMatchers(HttpMethod.POST, "/api/reviews/{id}").authenticated()
                .requestMatchers(HttpMethod.GET, "/api/reviews/{id}").permitAll()
                /*              FALTA POR TERMINAR
                .requestMatchers(HttpMethod.GET, "/api/reviews").permitAll()
                */
                //ShoppingCartAPI
                .requestMatchers(HttpMethod.GET, "/api/shoppingCart").hasRole("USER")
                .requestMatchers(HttpMethod.DELETE, "/api/shoppingCart/{id}").hasRole("USER")
                .requestMatchers(HttpMethod.POST, "/api/shoppingCart/{id}").hasRole("USER")
                .requestMatchers(HttpMethod.POST, "/api/shoppingCart/payment").hasRole("USER")

                //UserAPI
                .requestMatchers(HttpMethod.GET, "/api/users/{id}/image").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/users/{id}/image").permitAll()
                .requestMatchers(HttpMethod.DELETE, "/api/users/{id}/image").permitAll()
                .requestMatchers(HttpMethod.PUT, "/api/users/{id}/image").permitAll()
                .requestMatchers(HttpMethod.PUT, "/api/users").permitAll()
                .requestMatchers(HttpMethod.DELETE, "/api/users/{id}").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/users").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/users/{id}").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/users").permitAll()




                .anyRequest().permitAll()
            );
            http.formLogin(formLogin -> formLogin.disable());
            http.csrf(csrf -> csrf.disable());
            http.httpBasic(httpBasic -> httpBasic.disable());
            
            http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
            http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

            return http.build();





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
