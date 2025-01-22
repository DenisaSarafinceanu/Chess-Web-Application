package Chesllyly.Chesslyly.util;

import org.springframework.stereotype.Component;

@Component
public class CookieUtil {
    public jakarta.servlet.http.Cookie createHttpOnlyCookie(String name, String value, int maxAge) {
        jakarta.servlet.http.Cookie cookie = new jakarta.servlet.http.Cookie(name, value);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(maxAge);
        return cookie;
    }
}

