package com.example.ss_2023_c1_e1.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@Configuration
public class WebSecurityConfig {

    @Bean
    UserDetailsService userDetailsServce() {
        var uds = new InMemoryUserDetailsManager();
        var u1 = User.withUsername("bill")
        .password("1234")
        .authorities("read")
        .build();

        uds.createUser(u1);
        return uds;
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }
    
}
