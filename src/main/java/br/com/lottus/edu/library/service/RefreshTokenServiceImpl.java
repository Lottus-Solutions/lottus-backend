package br.com.lottus.edu.library.service;

import br.com.lottus.edu.library.exception.TokenRefreshException;
import br.com.lottus.edu.library.model.RefreshToken;
import br.com.lottus.edu.library.model.Usuario;
import br.com.lottus.edu.library.repository.RefreshTokenRepository;
import br.com.lottus.edu.library.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.antlr.v4.runtime.Token;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService{


    @Value("${jwt.refresh.token.expiration.default}")
    private Long refreshTokenDurationMsDefault;

    @Value("${jwt.refresh.token.expiration.rememberMe}")
    private Long refreshTokenDurationMsRememberMe;

    private final RefreshTokenRepository refreshTokenRepository;
    private final UsuarioRepository usuarioRepository;

    @Override
    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    @Override
    @Transactional
    public RefreshToken criarRefreshToken(Long userId, boolean rememberMe) {
        Usuario usuario = usuarioRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Erro: Usuário não encontrado com ID: " + userId + " para criar um token de acesso continuo"));

        refreshTokenRepository.findByUsuario(usuario)
                .ifPresent(refreshTokenRepository::delete);

        long durationMs = rememberMe ? refreshTokenDurationMsRememberMe : refreshTokenDurationMsDefault;

        RefreshToken refreshToken = new RefreshToken(
                usuario,
                UUID.randomUUID().toString(),
                Instant.now().plusMillis(durationMs)
        );

        return refreshTokenRepository.save(refreshToken);
    }

    @Override
    public RefreshToken verificarExpiracao(RefreshToken token) {
        if (token.getDataExpiracao().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.delete(token);
            throw new TokenRefreshException(token.getToken(), "Sessão expirada. Por favor, faça login novamente.");
        }
        return token;
    }

    @Override
    @Transactional
    public int deleteByUsuarioId(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Erro: Usuário não encontrado com ID: " + id + " para deletar o token de acesso continuo"));

        return refreshTokenRepository.deleteByUsuario(usuario);
    }

    @Override
    public void deletarTokensExpirados() {
        refreshTokenRepository.deleteByDataExpiracaoLessThan(Instant.now());

    }
}
