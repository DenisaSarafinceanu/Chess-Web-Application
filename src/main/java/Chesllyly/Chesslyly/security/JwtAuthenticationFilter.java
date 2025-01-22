package Chesllyly.Chesslyly.security;

import Chesllyly.Chesslyly.util.JwtUtil;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.http.Cookie;

import javax.servlet.annotation.WebFilter;

import java.io.IOException;

@Component
@WebFilter("/api/*")
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(jakarta.servlet.http.HttpServletRequest request, jakarta.servlet.http.HttpServletResponse response, jakarta.servlet.FilterChain filterChain) throws jakarta.servlet.ServletException, IOException {
        // Skip the filter for specific endpoints (like the refresh-token endpoint)
        String requestURI = request.getRequestURI();
        if (requestURI.equals("/refresh-token")) {
            filterChain.doFilter(request, response);
            return; // Skip further processing for refresh token endpoint
        }

        // Try to extract the access token from cookies
        Cookie[] cookies = request.getCookies();
        String accessToken = null;

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("access_token".equals(cookie.getName())) { // Check if the cookie name is access_token
                    accessToken = cookie.getValue(); // Extract the token value from the cookie
                    break; // No need to continue searching once we find the token
                }
            }
        }

        if (accessToken != null && jwtUtil.validateToken(accessToken)) {
            // Token is valid, extract user info and set authentication
            String userEmail = jwtUtil.extractUserEmail(accessToken);

            // Create an authentication token (you may need to update roles and authorities based on your setup)
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(userEmail, null, null);

            // Set the authentication in the SecurityContext
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } else {
            // If the token is invalid, respond with 401 Unauthorized
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Invalid or expired JWT access token.");
            return;  // End the request here if the token is invalid
        }

        // Continue the filter chain
        filterChain.doFilter(request, response);
    }

}
