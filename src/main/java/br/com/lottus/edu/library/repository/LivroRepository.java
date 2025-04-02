package br.com.lottus.edu.library.repository;

import br.com.lottus.edu.library.model.Livro;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LivroRepository extends JpaRepository<Livro, Long> {
}
