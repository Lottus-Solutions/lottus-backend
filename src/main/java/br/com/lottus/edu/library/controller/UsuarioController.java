package br.com.lottus.edu.library.controller;


import br.com.lottus.edu.library.model.Usuario;
import br.com.lottus.edu.library.security.CustomUserPrincipal;
import br.com.lottus.edu.library.security.SecurityConfig;
import br.com.lottus.edu.library.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.Authenticator;

@Tag(name = "Usuários", description = "Endpoints para gerenciamento de usuários")
@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @Operation(summary = "Obtém os dados do usuário logado")
    @GetMapping("/me")
    public ResponseEntity<Usuario> getUsuarioLogado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserPrincipal userPrincipal = (CustomUserPrincipal) authentication.getPrincipal();

        Usuario usuario = usuarioService.buscarPorEmail(userPrincipal.getEmail());

        usuario.setSenha(null);

        return ResponseEntity.ok(usuario);
    }
}
