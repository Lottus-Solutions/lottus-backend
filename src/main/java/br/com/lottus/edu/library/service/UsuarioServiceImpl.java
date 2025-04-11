package br.com.lottus.edu.library.service;

import br.com.lottus.edu.library.model.Usuario;
import br.com.lottus.edu.library.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl implements UsuarioService {
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public Usuario cadastrarUsuario(Usuario usuario) {
        usuario.setDtRegistro(new Date());
        // A senha já deve vir codificada do controller
        return usuarioRepository.save(usuario);
    }

    @Override
    @Transactional
    public Boolean removerConta(Usuario usuario) {
        usuarioRepository.delete(usuario);
        return true;
    }

    @Override
    public Usuario login(String email, String senha) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(email);
        
        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            if (passwordEncoder.matches(senha, usuario.getSenha())) {
                return usuario;
            }
        }
        
        return null;
    }

    @Override
    public Usuario buscarPorEmail(String email) {
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario com email " + email + " não encontrado"));
    }
}
