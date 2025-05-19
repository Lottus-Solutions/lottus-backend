package br.com.lottus.edu.library.repository;

import br.com.lottus.edu.library.model.RefreshToken;
import br.com.lottus.edu.library.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);

    Optional<RefreshToken> findByUsuario(Usuario usuario);

    @Modifying
    @Transactional
    int deleteByUsuario(Usuario usuario);

    @Modifying
    @Transactional
    void deleteByDataExpiracaoLessThan(Instant now);
}
