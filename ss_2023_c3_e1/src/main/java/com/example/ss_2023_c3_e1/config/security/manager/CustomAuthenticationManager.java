package com.example.ss_2023_c3_e1.config.security.manager;

import com.example.ss_2023_c3_e1.config.security.providers.CustomAuthenticationProviders;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class CustomAuthenticationManager implements AuthenticationManager {

    private final CustomAuthenticationProviders provider;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
         if (provider.supports(authentication.getClass())) {
             return provider.authenticate(authentication);
         }
         throw new BadCredentialsException("Oh no!");
    }
}
