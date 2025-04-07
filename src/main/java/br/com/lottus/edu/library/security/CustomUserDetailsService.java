package br.com.lottus.edu.library.security;

import br.com.lottus.edu.library.model.Usuario;
import br.com.lottus.edu.library.repository.UsuarioRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    public CustomUserDetailsService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(email);
        
        if (usuarioOpt.isEmpty()) {
            throw new UsernameNotFoundException("Usuário não encontrado com o email: " + email);
        }
        
        Usuario usuario = usuarioOpt.get();
        
        return new User(
            usuario.getEmail(),
            usuario.getSenha(),
            Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        );
    }
    
    public Usuario findUsuarioByEmail(String email) {
        return usuarioRepository.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado com o email: " + email));
    }
} 