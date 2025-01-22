package Chesllyly.Chesslyly.security;

import Chesllyly.Chesslyly.security.JwtAuthenticationFilter;
import Chesllyly.Chesslyly.util.CookieUtil;
import Chesllyly.Chesslyly.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

import java.io.IOException;

@Configuration
public class SecurityConfig {

    @Value("${frontend.base-url}")
    private String frontendBaseUrl;

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private CookieUtil cookieUtil;


    @Bean
    public SimpleUrlAuthenticationSuccessHandler successHandler() {
        return new SimpleUrlAuthenticationSuccessHandler() {
            @Override
            public void onAuthenticationSuccess(jakarta.servlet.http.HttpServletRequest request, jakarta.servlet.http.HttpServletResponse response, Authentication authentication) throws IOException {
                // Extract user details (e.g., email) from the authentication object
                OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();
                String userEmail = oauth2User.getAttribute("email");

                // Generate JWT tokens
                String accessToken = jwtUtil.generateAccessToken(userEmail); // Short-lived access token
                String refreshToken = jwtUtil.generateRefreshToken(userEmail); // Long-lived refresh token

//                // Redirect to the frontend with both tokens as query parameters
//                String redirectUrl = frontendBaseUrl + "/login-success?access_token=" + accessToken + "&refresh_token=" + refreshToken;
//                response.sendRedirect(redirectUrl);

                // Create HttpOnly cookies for tokens
                jakarta.servlet.http.Cookie accessTokenCookie = cookieUtil.createHttpOnlyCookie("access_token", accessToken, 3600); // 1 hour expiry
                jakarta.servlet.http.Cookie refreshTokenCookie = cookieUtil.createHttpOnlyCookie("refresh_token", refreshToken, 604800); // 30 days expiry

                // Add cookies to the response
                response.addCookie(accessTokenCookie);
                response.addCookie(refreshTokenCookie);

                // Redirect to the frontend
                response.sendRedirect(frontendBaseUrl + "/home");
            }
        };
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of(frontendBaseUrl)); // Replace with your frontend URL, e.g., "http://localhost:3000"
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS")); // Allow necessary HTTP methods
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type")); // Include Authorization header for JWT
        configuration.setExposedHeaders(List.of("Authorization")); // Expose headers if necessary
        configuration.setAllowCredentials(true); // Allow cookies and credentials (if needed)

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // Apply to all endpoints
        return source;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, ClientRegistrationRepository clientRegistrationRepository) throws Exception {
        // Create a custom authorization request resolver
        DefaultOAuth2AuthorizationRequestResolver authorizationRequestResolver =
                new DefaultOAuth2AuthorizationRequestResolver(clientRegistrationRepository, "/oauth2/authorization");

        // Set a customizer to add the `prompt=select_account` parameter
        authorizationRequestResolver.setAuthorizationRequestCustomizer(customizer ->
                customizer.additionalParameters(params -> params.put("prompt", "select_account"))
        );

        return http.cors(cors -> cors.configurationSource(corsConfigurationSource())) // Enable CORS
                .csrf(csrf -> csrf.disable()) // Disable CSRF for simplicity
                .authorizeHttpRequests(registry -> {
                    registry.requestMatchers("/", "refresh-token").permitAll();
                    registry.anyRequest().authenticated();
                })
                .oauth2Login(oauth2 -> oauth2
                        .authorizationEndpoint(authorization ->
                                authorization.authorizationRequestResolver(authorizationRequestResolver))
                        .successHandler(successHandler())
                )
                .formLogin(Customizer.withDefaults())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class) // Add your custom filter
                .build();
    }
}
