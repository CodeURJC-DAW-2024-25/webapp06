package es.codeurjc.global_mart.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
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
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }


    @Bean
    public DaoAuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();

        authenticationProvider.setUserDetailsService(userDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEncoder());

        return authenticationProvider;

    }

    // @Bean
    // public InMemoryUserDetailsManager inMemoryUserDetailsManager(){
    //     UserDetails user = User.builder()
    //             .username("user")
    //             .password(passwordEncoder().encode("password"))
    //             .roles("USER")
    //             .build();

    //     UserDetails company = User.builder()
    //             .username("company")
    //             .password(passwordEncoder().encode("password"))
    //             .roles("COMPANY")
    //             .build();
    //     UserDetails admin = User.builder()
    //             .username("admin")
    //             .password(passwordEncoder().encode("password"))
    //             .roles("ADMIN")
    //             .build();
    //     return new InMemoryUserDetailsManager(user, company, admin);
    // }


    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        
        http.authenticationProvider(authenticationProvider());
        http.authorizeHttpRequests(authorize -> authorize
                    // PUBLIC PAGES
                    .requestMatchers("/").permitAll()
                    .requestMatchers("/about").permitAll()
                    .requestMatchers("/choose_login_option").permitAll()
                    .requestMatchers("/register").permitAll()
                    .requestMatchers("/login").permitAll()
                    .requestMatchers("/allProducts").permitAll()
                    .requestMatchers("/product/{id}").permitAll()
                    .requestMatchers("/descriptionProduct").permitAll()
                    // PRIVATE PAGES
                    .requestMatchers("/uploadProducts").hasAnyRole("COMPANY")
                    .requestMatchers("/administrator").hasAnyRole("ADMIN")
                    .requestMatchers("/shoppingcart").hasAnyRole("USER")
                    .requestMatchers("/user").hasAnyRole("USER"))
                .formLogin(formLogin -> formLogin
                    .loginPage("/login")
                    .defaultSuccessUrl("/")
                    .permitAll())
                .logout(logout -> logout
                    .logoutUrl("/logout")
                    .logoutSuccessUrl("/")
                    .permitAll());
        return http.build();
            
    }

    

}

