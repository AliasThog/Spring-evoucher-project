package com.example.evoucherproject.auth.jwt;

import com.example.evoucherproject.exception.CustomException;
import com.example.evoucherproject.ultil.RequestUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;

@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private UserDetailsService customUserDetailsService;

    @Value("${user_to_access_url}")
    private String[] userAccessUrls;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String path = request.getRequestURI();
        if (isAccessAllowed(path)) {
            filterChain.doFilter(request, response);
            return;
        }
        try {
            String jwt = RequestUtils.getTokenFromRequest(request);
            if (jwt.equals("token_null") || !jwtTokenProvider.validateToken(jwt)) {
                throw new CustomException("token ko hop le", HttpStatus.UNAUTHORIZED);
            }
            String username = jwtTokenProvider.getUsernameFromToken(jwt);

            UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);
            // mục đích đại diện người dùng đã được xác thực
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities());
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (CustomException e) {
            SecurityContextHolder.clearContext();
            response.setContentType("application/json");
            response.setStatus(e.getHttpStatus().value());
            response.getWriter().write(e.getMessage());
            return;
        }

        filterChain.doFilter(request, response);
    }
    private boolean isAccessAllowed(String path) {
        return Arrays.stream(userAccessUrls)
                .anyMatch(url -> url.equals(path));
    }
}