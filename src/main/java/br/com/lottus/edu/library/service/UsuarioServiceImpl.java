package br.com.lottus.edu.library.service;

import br.com.lottus.edu.library.dto.ResponseSolicitarReset;
import br.com.lottus.edu.library.dto.UsuarioDTO;
import br.com.lottus.edu.library.exception.EmailJaCadastradoException;
import br.com.lottus.edu.library.exception.UsuarioNaoEncontradoException;
import br.com.lottus.edu.library.model.PasswordResetToken;
import br.com.lottus.edu.library.model.Usuario;
import br.com.lottus.edu.library.repository.PasswordResetTokenRepository;
import br.com.lottus.edu.library.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl implements UsuarioService {
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final AlunoServiceImpl alunoService;
    private final EmprestimoServiceImpl emprestimoService;

    private final PasswordResetTokenRepository passwordResetTokenRepository;

    @Override
    @Transactional
    public Usuario cadastrarUsuario(Usuario usuario) {
        usuario.setDtRegistro(new Date());

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

    @Override
    public void renovarSemestre() {
        alunoService.resetarBonus();
        alunoService.resetarLivrosLidos();
        emprestimoService.resetarStatus();
    }

        @Override
        @Transactional
        public ResponseSolicitarReset solicitarResetSenha(String email) {
            Usuario usuario = usuarioRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Usuario com email " + email + " não encontrado"));

            PasswordResetToken passwordResetToken = new PasswordResetToken();

            passwordResetToken.setUsuario(usuario);

            passwordResetTokenRepository.save(passwordResetToken);

            String publicToken = passwordResetToken.getPublicToken().toString();

            String token = passwordResetToken.getToken().toString();

            return new ResponseSolicitarReset("http://localhost:8080/auth/resetar-senha?ticket="
                    + publicToken, token);

        }

    @Override
    @Transactional
    public Boolean resetarSenha(String token, String novaSenha) {

        UUID tokenIn = UUID.fromString(token);

        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(tokenIn)
                .orElseThrow(()-> new RuntimeException("Token inválido, faça a solicitação novamente"));

        if (resetToken.getExpiresAt().isBefore(Instant.now())) {
            passwordResetTokenRepository.delete(resetToken);
            throw new RuntimeException("Token expirado, faça a solicitação novamente");
        }

        if(resetToken.getUsed()){
            passwordResetTokenRepository.delete(resetToken);
            throw new RuntimeException("Token inválido, faça a solicitação novamente");
        }

        Usuario usuario = resetToken.getUsuario();

        usuario.setSenha(novaSenha);

        passwordResetTokenRepository.delete(resetToken);

        usuarioRepository.save(usuario);

        return true;
    }

    @Override
    public UsuarioDTO editarUsuario(Long id, Usuario usuarioAtualizado) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(UsuarioNaoEncontradoException::new);

        if (usuarioRepository.existsByEmailAndIdNot(usuarioAtualizado.getEmail(), id)) {
            throw new EmailJaCadastradoException();
        }

        usuario.setNome(usuarioAtualizado.getNome());
        usuario.setEmail(usuarioAtualizado.getEmail());

        if (usuarioAtualizado.getSenha() != null && !usuarioAtualizado.getSenha().isBlank()) {
            usuario.setSenha(passwordEncoder.encode(usuarioAtualizado.getSenha()));
        }

        usuarioRepository.save(usuario);
        return new UsuarioDTO(usuarioAtualizado.getNome(), usuarioAtualizado.getEmail());
    }

}
