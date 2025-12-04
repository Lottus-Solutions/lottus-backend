package br.com.lottus.edu.library.controller;

import br.com.lottus.edu.library.dto.*;
import br.com.lottus.edu.library.model.Usuario;
import br.com.lottus.edu.library.repository.UsuarioRepository;
import br.com.lottus.edu.library.security.CustomUserDetailsService;
import br.com.lottus.edu.library.security.util.JwtUtil;
import br.com.lottus.edu.library.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
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

    @Operation(summary = "Registra um novo usuário", description = "Retorna o usuário cadastrado sem a senha(senha cadastrada criptografada)")
    @PostMapping("/register")
    public ResponseEntity<Usuario> registrarUsuario(@RequestBody Usuario usuario) {
        // Encodar a senha antes de salvar
        usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
        Usuario novoUsuario = usuarioService.cadastrarUsuario(usuario);
        return ResponseEntity.status(HttpStatus.CREATED).body(novoUsuario);
    }

    @Operation(summary = "Realiza login do usuário", description = "Retorna o token JWT do usuário")
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        try {
            // Autentica o usuário pelo email e senha
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getSenha())
            );
            
            // Se chegar aqui, a autenticação foi bem-sucedida
            // Busca o usuário para gerar o token que inclui o ID
            Usuario usuario = userDetailsService.findUsuarioByEmail(request.getEmail());
            String token = jwtUtil.generateToken(usuario);
            
            return ResponseEntity.ok(new AuthResponse(token));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new AuthResponse(null));
        }
    }
    
    @Operation(summary = "Verifica se um token é válido", description = "Retorna informações do usuário se o token for válido")
    @PostMapping("/validate")
    public ResponseEntity<Map<String, Object>> validateToken(@RequestHeader("Authorization") String authHeader) {
        // Extrair o token do cabeçalho
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            
            if (jwtUtil.validateToken(token)) {
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

    @PostMapping("/esqueci-senha")
    @Operation(summary = "Solicita reset de senha", description = "Retorna um link para resetar a senha e token privado identificador para validaçao do metodo")
    public ResponseEntity<ResponseSolicitarReset> solicitarResetSenha(@RequestBody RequestSolicitarResetSenha request){

        String email = request.email();

        return ResponseEntity.ok(usuarioService.solicitarResetSenha(email));
    }

    @PutMapping("/resetar-senha")
    @Operation(summary = "Reseta a senha do usuario", description = "Retorna o resultado da operação com true ou false")
    public ResponseEntity<Boolean> resetarSenha(@RequestBody @Valid RequestNovaSenha requestNovaSenha){

        String senha = passwordEncoder.encode(requestNovaSenha.senha());

        return ResponseEntity.ok(usuarioService.resetarSenha(requestNovaSenha.token(), senha));
    }
}
