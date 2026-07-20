package com.nonmus.nonmus.filter;

import java.io.IOException;
import java.util.Collections;

import com.nonmus.nonmus.modules.user.entity.Users;
import com.nonmus.nonmus.modules.user.enums.Provider;
import com.nonmus.nonmus.modules.user.service.UsersService;
import com.nonmus.nonmus.security.AuthenticatedUser;
import jakarta.servlet.http.Cookie;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.nonmus.nonmus.modules.common.util.JwtUtil;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String ACCESS_TOKEN_COOKIE = "access_token";


    private final JwtUtil jwtUtil;
    private final UsersService usersService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String token = extractToken(request);
        if (token == null) {
            filterChain.doFilter(request, response);
            return;
        }

        if (SecurityContextHolder.getContext().getAuthentication() == null && jwtUtil.isTokenValid(token)) {
            String email = jwtUtil.getEmailFromToken(token);

            Users user = usersService.getUsersByEmail(email).orElse(null);

            if(user != null) {
                AuthenticatedUser authenticatedUser = new AuthenticatedUser();
                authenticatedUser.setEmail(user.getUsername());

                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        authenticatedUser,
                        null,
                        Collections.emptyList());

                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        filterChain.doFilter(request, response);
    }

    private String extractToken(HttpServletRequest request) {
//        // 1. Authorization Header
//        String authHeader = request.getHeader("Authorization");
//        if (authHeader != null && authHeader.startsWith("Bearer ")) {
//            return authHeader.substring(7);
//        }

        // 2. HttpOnly Cookie
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (ACCESS_TOKEN_COOKIE.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }

        return null;
    }
}