package br.com.lottus.edu.library.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.stream.Collectors;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RequestLoggingFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(RequestLoggingFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        try {
            // Registra detalhes da requisição
            logger.info("=== REQUISIÇÃO RECEBIDA ===");
            logger.info("Método: {}", request.getMethod());
            logger.info("URI: {}", request.getRequestURI());
            logger.info("Query: {}", request.getQueryString());
            logger.info("Cabeçalhos: {}", Collections.list(request.getHeaderNames())
                    .stream()
                    .map(headerName -> headerName + ": " + request.getHeader(headerName))
                    .collect(Collectors.joining(", ")));
            
            // Continua o processamento da requisição
            filterChain.doFilter(request, response);
            
            // Registra detalhes da resposta
            logger.info("=== RESPOSTA ENVIADA ===");
            logger.info("Status: {}", response.getStatus());
        } catch (Exception e) {
            logger.error("Erro durante o processamento da requisição: ", e);
            throw e;
        }
    }
} 