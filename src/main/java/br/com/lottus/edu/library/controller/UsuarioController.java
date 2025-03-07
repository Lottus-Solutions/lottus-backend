package br.com.lottus.edu.library.controller;

import br.com.lottus.edu.library.model.Usuario;
import br.com.lottus.edu.library.repository.UsuarioRepository;
import br.com.lottus.edu.library.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;
import java.util.Optional;

@Tag(name = "Usuários", description = "Endpoint para gerenciamento de usuarios")
@RestController
@RequestMapping("/usuarios")
public class UsuarioController {


    @Autowired
    private final UsuarioService usuarioService;

    @Autowired
    private final UsuarioRepository usuarioRepository;

    public UsuarioController(UsuarioService usuarioService, UsuarioRepository usuarioRepository) {
        this.usuarioService = usuarioService;
        this.usuarioRepository = usuarioRepository;
    }

    @Operation(summary = "Cadastra um novo usuario")
    // ✅ retorno de 201 Created
    @PostMapping
    public ResponseEntity<Usuario> cadastrarUsuario(@RequestBody Usuario usuario) {
        Usuario novoUsuario = usuarioService.cadastrarUsuario(usuario);
        return ResponseEntity.status(HttpStatus.CREATED).body(novoUsuario);
    }
    @Operation(summary = "Remove um usuario pelo ID")
    // ✅ retorno de 404 Not Found se o usuário não existir
    @DeleteMapping("deletarUsuario/{id}")
    public ResponseEntity<String> removerUsuario(@PathVariable Long id) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findById(id);

        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Usuário não encontrado");
        }

        boolean removido = usuarioService.removerConta(usuarioOpt.get());

        if (removido) {
            return ResponseEntity.ok("Usuário removido com sucesso");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao remover o usuário");
        }
    }

    @Operation(summary = "Realiza login de um usuario")
    // ✅ retorno de 200 OK se sucesso, 401 Unauthorized se falha
    @PostMapping("/login")
    public ResponseEntity<Usuario> login(@RequestBody Map<String, String> requestBody) {
        String email = requestBody.get("email");
        String senha = requestBody.get("senha");

        return usuarioRepository.findByEmailAndSenha(email, senha)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .build()); // 401 Unauthorized para login inválido
    }
}
