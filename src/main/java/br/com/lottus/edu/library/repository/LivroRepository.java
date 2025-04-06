package br.com.lottus.edu.library.repository;

import br.com.lottus.edu.library.model.Livro;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LivroRepository extends JpaRepository<Livro, Long> {
    Optional<Livro> findByNomeContaining(String nome);
}
