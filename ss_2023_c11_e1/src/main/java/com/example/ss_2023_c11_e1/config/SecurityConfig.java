package com.example.ss_2023_c11_e1.config;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.UUID;

// (GET) http://localhost:8080/oauth2/authorize?response_type=code&client_id=client&scope=openid&redirect_uri=https://springone.io/authorized&code_challenge=ZOMulJKNE3Wcwt7iPVjtNf98NrtlCcmAat9if9QEnRI&code_challenge_method=S256
// (POST) http://localhost:8080/oauth2/token?client_id=client&code=xjYyUeWkOnQqZuZK-wbs_ndZnxJcp-Wl9vlaSbbTJUTNmokGyt936sgnvB6ZRddi8LE_RfVJM82h9BVZMlplC7K53tUqvFYJtguXIJ78gg_DgOVn7tuloRRxwJRCqs37&code_verifier=MrcMK770gIeITu2JDH6Tto86li7MgA5PAoT5trWIQ7A&grant_type=authorization_code&redirect_uri=https://springone.io/authorized
// Along with the above url, we have to provide client ID and secret in the Authorization basic input

@Configuration
public class SecurityConfig {

    @Bean
    @Order(1)
    public SecurityFilterChain authServerSecurityFilterChain(HttpSecurity http) throws Exception {
        OAuth2AuthorizationServerConfiguration.applyDefaultSecurity(http);

        http.getConfigurer(OAuth2AuthorizationServerConfigurer.class)
                .oidc(Customizer.withDefaults());

        http.exceptionHandling(
                e -> e.authenticationEntryPoint(
                        new LoginUrlAuthenticationEntryPoint("/login")
                )
        );
        return http.build();
    }

    @Bean
    @Order(2)
    public SecurityFilterChain appSecurityFilterChain(HttpSecurity http) throws Exception {
        http.formLogin(Customizer.withDefaults())
                .authorizeHttpRequests(x -> x.anyRequest().authenticated());

        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        var u1 = User.withUsername("user")
                .password("password")
                .authorities("read")
                .build();
        return new InMemoryUserDetailsManager(u1);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }

    @Bean
    public RegisteredClientRepository registeredClientRepository() {
        RegisteredClient r1 = RegisteredClient.withId(UUID.randomUUID().toString())
                .clientId("client")
                .clientSecret("secret")
                .scope(OidcScopes.OPENID)
                .scope(OidcScopes.PROFILE)
                .redirectUri("https://springone.io/authorized")
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                .build();
        return new InMemoryRegisteredClientRepository(r1);
    }

    @Bean
    public AuthorizationServerSettings authorizationServerSettings() {
        return AuthorizationServerSettings.builder()
                .build();
    }

    @Bean
    public JWKSource<SecurityContext> jwkSource() throws Exception {
        KeyPairGenerator kg = KeyPairGenerator.getInstance("RSA");
        kg.initialize(2048);

        KeyPair kp = kg.generateKeyPair();

        RSAPublicKey publicKey = (RSAPublicKey) kp.getPublic();
        RSAPrivateKey privateKey = (RSAPrivateKey) kp.getPrivate();

        RSAKey key = new RSAKey.Builder(publicKey)
                .privateKey(privateKey)
                .keyID(UUID.randomUUID().toString())
                .build();
        JWKSet set = new JWKSet(key);
        return new ImmutableJWKSet<>(set);
    }
}
// Creating code verifier
/**
 * import java.security.SecureRandom;
 * import java.util.Base64;
 *
 *     SecureRandom sr = new SecureRandom();
 * byte [] code = new byte[32];
 *
 * sr.nextBytes(code);
 *
 * String code_verifier = Base64.getUrlEncoder()
 *         .withoutPadding()
 *         .encodeToString(code);
 */



// Creating code challenge
/**
 * import java.security.MessageDigest;
 * import java.security.NoSuchAlgorithmException;
 * import java.util.Base64;
 *
 *     try {
 *         MessageDigest md = MessageDigest.getInstance("SHA-256");
 *         byte [] digested = md.digest("MrcMK770gIeITu2JDH6Tto86li7MgA5PAoT5trWIQ7A".getBytes());
 *         String code_challenge = Base64.getUrlEncoder()
 *                 .withoutPadding()
 *                 .encodeToString(digested);
 *         System.out.println(code_challenge);
 *     } catch (NoSuchAlgorithmException e) {
 *         throw new RuntimeException(e);
 *     }
 */
