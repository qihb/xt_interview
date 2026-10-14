package com.xt.interview.filter;

import com.xt.interview.util.JwtUtil;
import com.xt.interview.entity.Account;
import com.xt.interview.repository.AccountRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Objects;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AccountRepository accountRepository;

    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        return pathMatcher.match("/api/auth/**", path);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String servletPath = request.getServletPath();
        String authorization = request.getHeader("Authorization");

        if (authorization == null || !authorization.startsWith("Bearer ")) {
            sendErrorResponse(response, 401, "未授权访问，请先登录");
            return;
        }

        String token = authorization.substring(7);

        try {
            boolean valid = jwtUtil.isTokenValid(token);
            if (!valid) {
                sendErrorResponse(response, 401, "token已过期，请重新登录");
                return;
            }

            String username = jwtUtil.getUsernameFromToken(token);
            Long userId = jwtUtil.getUserIdFromToken(token);
            Integer roleLevel = jwtUtil.getRoleLevelFromToken(token);
            Integer tokenVersion = jwtUtil.getTokenVersionFromToken(token);
            Account account = accountRepository.findByIdAndDeleted(userId, 0).orElse(null);

            if (account == null
                    || !Objects.equals(account.getTokenVersion(), tokenVersion)
                    || !account.getUsername().equals(username)
                    || !Objects.equals(account.getStatus(), 1)) {
                sendErrorResponse(response, 401, "token已失效，请重新登录");
                return;
            }

            request.setAttribute("userId", userId);
            request.setAttribute("username", username);
            request.setAttribute("roleLevel", roleLevel);

        } catch (Exception e) {
            sendErrorResponse(response, 401, "token无效，请重新登录");
            return;
        }

        filterChain.doFilter(request, response);
    }

    private void sendErrorResponse(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json;charset=UTF-8");

        String escapedMessage = escapeJson(message);
        String jsonResponse = String.format(
            "{\"success\":false,\"message\":\"%s\",\"code\":%d}",
            escapedMessage, status
        );

        response.getWriter().write(jsonResponse);
    }

    private String escapeJson(String str) {
        if (str == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            switch (c) {
                case '\\': sb.append("\\\\"); break;
                case '"': sb.append("\\\""); break;
                case '\n': sb.append("\\n"); break;
                case '\r': sb.append("\\r"); break;
                case '\t': sb.append("\\t"); break;
                default: sb.append(c);
            }
        }
        return sb.toString();
    }
}
