package br.com.lottus.edu.library.repository;

import br.com.lottus.edu.library.model.PasswordResetToken;
import br.com.lottus.edu.library.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {

    Optional<PasswordResetToken> findByToken(UUID token);

    boolean findByUsuario(Usuario usuario);

}
