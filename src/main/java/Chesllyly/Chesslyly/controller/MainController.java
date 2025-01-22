package Chesllyly.Chesslyly.controller;

import Chesllyly.Chesslyly.util.CookieUtil;
import Chesllyly.Chesslyly.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;

import jakarta.servlet.http.Cookie;

import java.security.Principal;

@RestController
public class MainController {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private CookieUtil cookieUtil;

    @RequestMapping("/")
    public String home() {
        return "Welcome!";
    }

    @RequestMapping("/api/user")
    public Principal user(Principal user) {
        return user;
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshAccessToken(jakarta.servlet.http.HttpServletRequest request, jakarta.servlet.http.HttpServletResponse response) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("refresh_token".equals(cookie.getName())) {
                    String refreshToken = cookie.getValue();
                    if (jwtUtil.validateToken(refreshToken)) {
                        String userEmail = jwtUtil.extractUserEmail(refreshToken);
                        String newAccessToken = jwtUtil.generateAccessToken(userEmail);
                        Cookie accessTokenCookie = cookieUtil.createHttpOnlyCookie("access_token", newAccessToken, 3600);
                        response.addCookie(accessTokenCookie);
                        return ResponseEntity.ok().build();
                    } else {
                        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid refresh token");
                    }
                }
            }
        }

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Refresh token not found");
    }

}