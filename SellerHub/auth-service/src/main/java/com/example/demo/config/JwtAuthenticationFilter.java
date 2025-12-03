package com.example.demo.config;

import com.example.demo.service.CustomUserDetailsService;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtUtil jwtUtil, CustomUserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {

        String path = request.getServletPath();
        System.out.println("=== JWT FILTER START for path: " + path + " ===");

        if (path.startsWith("/auth")) {

            System.out.println("Путь публичный, фильтр пропускает проверку токена");
            chain.doFilter(request, response);
            return;
        }

        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        System.out.println("Authorization header: " + header);

        if (StringUtils.hasText(header) && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            System.out.println("JWT токен получен: " + token);

            try {
                String username = jwtUtil.usernameFromAccess(token);
                System.out.println("Имя пользователя из токена: " + username);

                if (username != null &&
                        SecurityContextHolder.getContext().getAuthentication() == null &&
                        jwtUtil.isAccessTokenValid(token)) {

                    var userDetails = userDetailsService.loadUserByUsername(username);

                    var authorities = jwtUtil.rolesFromAccess(token)
                            .stream()
                            .map(role -> role.startsWith("ROLE_") ? role : "ROLE_" + role)
                            .map(SimpleGrantedAuthority::new)
                            .toList();

                    System.out.println("Роли из токена: " + authorities);

                    var auth = new UsernamePasswordAuthenticationToken(userDetails, null, authorities);
                    auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(auth);

                    System.out.println("Аутентификация установлена в SecurityContext");
                } else {
                    System.out.println("JWT токен недействителен или аутентификация уже установлена");
                }
            } catch (JwtException | IllegalArgumentException e) {
                System.out.println("Ошибка при разборе JWT: " + e.getMessage());
            }
        } else {
            System.out.println("Заголовок Authorization отсутствует или имеет неверный формат");
        }

        System.out.println("=== JWT FILTER END for path: " + path + " ===");
        chain.doFilter(request, response);
    }
}
