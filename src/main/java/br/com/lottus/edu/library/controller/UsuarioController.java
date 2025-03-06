package br.com.lottus.edu.library.controller;


import br.com.lottus.edu.library.model.Usuario;
import br.com.lottus.edu.library.repository.UsuarioRepository;
import br.com.lottus.edu.library.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

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


    @PostMapping
    public Usuario cadastrarUsuario(@RequestBody Usuario usuario){
        return usuarioService.cadastrarUsuario(usuario);
    }

    @DeleteMapping("deletarUsuario/{id}")
    public Boolean removerUsuario(@PathVariable Long id){
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario não encontrado"));
        return usuarioService.removerConta(usuario);
    }

    @PostMapping("/login")
    public Usuario login(@RequestBody Map<String, String> requestBody) {
        String email = requestBody.get("email");
        String senha = requestBody.get("senha");

        System.out.println("EMAIL: " + email);
        System.out.println("SENHA: " + senha);

        return usuarioRepository.findByEmailAndSenha(email, senha)
                .orElseThrow(() -> new RuntimeException("Usuário ou senha inválidos"));
    }


}
