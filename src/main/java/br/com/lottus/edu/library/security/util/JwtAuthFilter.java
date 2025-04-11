package br.com.lottus.edu.library.security.util;

import br.com.lottus.edu.library.security.CustomUserDetailsService;
import br.com.lottus.edu.library.security.CustomUserPrincipal;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;
    private static final Logger logger = LoggerFactory.getLogger(JwtAuthFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException,
            IOException {
        logger.info("=== INÍCIO DO FILTRO JWT - URL: {} ===", request.getRequestURI());
        final String authHeader = request.getHeader("Authorization");

        logger.info("Header de Authorization recebido: {}", authHeader);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            logger.warn("Header de autorização ausente ou inválido. Continuando a cadeia de filtros sem autenticação.");
            filterChain.doFilter(request, response);
            return;
        }
        
        try {
            final String token = authHeader.substring(7);
            logger.info("Token extraído: {}", token.substring(0, Math.min(10, token.length())) + "...");
            
            final String userEmail = jwtUtil.extractEmail(token);
            logger.info("Email extraído do token: {}", userEmail);
            
            if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                logger.info("Carregando detalhes do usuário para o email: {}", userEmail);
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);
                
                logger.info("Validando token...");
                if (jwtUtil.validateToken(token, userDetails)) {
                    logger.info("Token válido para o usuário: {}", userEmail);
                    // Criamos um objeto que contém email e ID do usuário
                    Long userId = jwtUtil.extractUserId(token);
                    logger.info("ID do usuário extraído: {}", userId);
                    
                    CustomUserPrincipal userPrincipal = new CustomUserPrincipal(
                        userDetails.getUsername(), 
                        userId
                    );
                    
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userPrincipal,
                        null,
                        userDetails.getAuthorities()
                    );
                    
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    logger.info("Autenticação definida no SecurityContextHolder com sucesso");
                } else {
                    logger.warn("Token inválido para o usuário: {}", userEmail);
                }
            } else {
                if (userEmail == null) {
                    logger.warn("Email não encontrado no token");
                }
                if (SecurityContextHolder.getContext().getAuthentication() != null) {
                    logger.info("Usuário já está autenticado");
                }
            }
        } catch (Exception e) {
            logger.error("Erro durante processamento do token JWT: {}", e.getMessage(), e);
        }

        logger.info("=== FIM DO FILTRO JWT ===");
        filterChain.doFilter(request, response);
    }
}
