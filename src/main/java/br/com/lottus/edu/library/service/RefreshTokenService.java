package br.com.lottus.edu.library.service;

import br.com.lottus.edu.library.model.RefreshToken;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface RefreshTokenService {

    Optional<RefreshToken> findByToken(String token);

    @Transactional
    RefreshToken criarRefreshToken(Long userId, boolean rememberMe);

    RefreshToken verificarExpiracao(RefreshToken token);

    @Transactional
    int deleteByUsuarioId(Long id);

    @Transactional
    void deletarTokensExpirados();


}
