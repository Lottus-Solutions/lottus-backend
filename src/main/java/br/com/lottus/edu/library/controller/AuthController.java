package br.com.lottus.edu.library.controller;

import br.com.lottus.edu.library.dto.AuthRequest;
import br.com.lottus.edu.library.dto.AuthResponse;
import br.com.lottus.edu.library.exception.TokenRefreshException;
import br.com.lottus.edu.library.model.RefreshToken;
import br.com.lottus.edu.library.model.Usuario;
import br.com.lottus.edu.library.repository.UsuarioRepository;
import br.com.lottus.edu.library.security.CustomUserDetailsService;
import br.com.lottus.edu.library.security.CustomUserPrincipal;
import br.com.lottus.edu.library.security.util.JwtUtil;
import br.com.lottus.edu.library.service.RefreshTokenService;
import br.com.lottus.edu.library.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.aspectj.apache.bcel.ExceptionConstants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@Tag(name = "Autenticação", description = "Endpoints para cadastro e autenticação de usuários")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UsuarioService usuarioService;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService userDetailsService;
    private final RefreshTokenService refreshTokenService;

    @Value("${jwt.refresh.token.cookie.name}")
    private String refreshTokenCookieName;

    @Value("${jwt.refresh.token.expiration.default}")
    private Long refreshTokenDurationMsDefault;

    @Value("${jwt.refresh.token.expiration.rememberMe}")
    private Long refreshTokenDurationMsRememberMe;


    @Operation(summary = "Registra um novo usuário", description = "Retorna o usuário cadastrado sem a senha(senha cadastrada criptografada)")
    @PostMapping("/register")
    public ResponseEntity<Usuario> registrarUsuario(@RequestBody Usuario usuario) {
        // Encodar a senha antes de salvar
        usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
        Usuario novoUsuario = usuarioService.cadastrarUsuario(usuario);
        return ResponseEntity.status(HttpStatus.CREATED).body(novoUsuario);
    }

    @Operation(summary = "Realiza login do usuário", description = "Retorna o token de acesso JWT do usuário no corpo da resposta e o token de refresh em um HttpOnly Cookie")
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        try {

            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getSenha())
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            Usuario usuario = userDetailsService.findUsuarioByEmail(request.getEmail());


            String acessToken = jwtUtil.generateAcessToken(usuario);
            RefreshToken refreshToken = refreshTokenService.criarRefreshToken(usuario.getId(), request.getRememberMe());

            long cookieMaxAgeSeconds = (request.getRememberMe() ? refreshTokenDurationMsRememberMe : refreshTokenDurationMsDefault) / 1000;

            ResponseCookie jwtRefreshCookie = ResponseCookie.from(refreshTokenCookieName, refreshToken.getToken())
                    .httpOnly(true)
                    .secure(false) // TODO: Mudar para true em produção
                    .path("/auth/refresh")
                    .maxAge(cookieMaxAgeSeconds)
                    .sameSite("Lax")
                    .build();

            
            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, jwtRefreshCookie.toString())
                    .body(new AuthResponse(acessToken)


            );
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new AuthResponse(null));
        }
    }


    @Operation(summary = "Atualiza o token de acesso usando um refresh token",
            description = "O refresh token é lido de um HttpOnly cookie")
    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@CookieValue(name = "${jwt.refresh.token.cookie.name}",
            required = false) String refreshTokenValueFromCookie,
                                          HttpServletRequest request){

        if(refreshTokenValueFromCookie == null || refreshTokenValueFromCookie.isEmpty()){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Valor do cookie não encontrado"));
        }

        try{
            return refreshTokenService.findByToken(refreshTokenValueFromCookie)
                    .map(refreshTokenService::verificarExpiracao)
                    .map(RefreshToken::getUsuario)
                    .map(usuario -> {
                        String newAcessToken = jwtUtil.generateAcessToken(usuario);

                        return ResponseEntity.ok(new AuthResponse(newAcessToken));
                    })
                    .orElseThrow(() -> {
                        return new TokenRefreshException(refreshTokenValueFromCookie, "Refresh token inválido");
                    });
        }catch (TokenRefreshException e){

            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", e.getMessage()));
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Erro inesperado ao atualizar o refresh token"));
        }

    }

    @Operation(summary = "Realiza logout do usuário", description = "Invalida o refresh token no servidor e limpa o cookie")
    @PostMapping("/logout")
    public ResponseEntity<?> loggoutUser(@AuthenticationPrincipal CustomUserPrincipal principal,
                                         @CookieValue(name = "${jwt.refresh.token.cookie.name}",
                                                 required = false) String refreshTokenValueFromCookie) {


        if(principal != null && principal.getUserId() != null){
            try{
                refreshTokenService.deleteByUsuarioId(principal.getUserId());
            }catch(Exception e){
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(Map.of("message", "Erro inesperado ao fazer logout"));
            }
        }

        ResponseCookie emptyCookie = ResponseCookie.from(refreshTokenCookieName, null)

                .httpOnly(true)
                .secure(false)
                .path("auth/refresh")
                .maxAge(0) //Expirar automaticamente
                .sameSite("Lax")
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, emptyCookie.toString())
                .body(Map.of("message", "Logout realizado com sucesso! O cookie de sessão foi limpo."));
    }

    
    @Operation(summary = "Verifica se um token é válido", description = "Retorna informações do usuário se o token for válido")
    @PostMapping("/validate")
    public ResponseEntity<Map<String, Object>> validateToken(@RequestHeader("Authorization") String authHeader) {
        // Extrair o token do cabeçalho
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            
            if (jwtUtil.validateToken(token)) {
                // Extrair informações do token
                String email = jwtUtil.extractEmail(token);
                Long userId = jwtUtil.extractUserId(token);
                
                return ResponseEntity.ok(Map.of(
                    "valid", true,
                    "userId", userId,
                    "email", email
                ));
            }
        }
        
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("valid", false));
    }
}
