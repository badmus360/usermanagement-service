//package com.fintech.usermanagement.jwt;
//
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import lombok.NonNull;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.stereotype.Component;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import java.io.IOException;
//import java.util.Collections;
//
//@Slf4j
//@Component
//@RequiredArgsConstructor
//public class JwtAuthenticationFilter extends OncePerRequestFilter {
//
//    private final JwtUtils jwtUtils;
//
//    @Override
//    protected void doFilterInternal(
//            @NonNull HttpServletRequest request,
//            @NonNull HttpServletResponse response,
//            @NonNull FilterChain filterChain
//    ) throws ServletException, IOException {
//
//        final String authHeader = request.getHeader("Authorization");
//        final String token;
//        final String username;
//
//        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
//            filterChain.doFilter(request, response);
//            return;
//        }
//
//        try {
//            token = authHeader.substring(7);
//            username = jwtUtils.getUsername(token);
//
//            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
//                if (jwtUtils.validateToken(token)) {
//                    var authToken = new UsernamePasswordAuthenticationToken(
//                            username,
//                            null,
//                            Collections.emptyList()
//                    );
//                    SecurityContextHolder.getContext().setAuthentication(authToken);
//                }
//            }
//        } catch (Exception e) {
//            log.error("JWT validation error: {}", e.getMessage());
//            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid token");
//            return;
//        }
//
//        filterChain.doFilter(request, response);
//    }
//}