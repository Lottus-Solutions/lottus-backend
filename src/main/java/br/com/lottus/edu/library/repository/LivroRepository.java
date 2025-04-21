package br.com.lottus.edu.library.repository;

import br.com.lottus.edu.library.model.Categoria;
import br.com.lottus.edu.library.model.Livro;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LivroRepository extends JpaRepository<Livro, Long> {
    List<Livro> findByNomeContaining(String nome);
    List<Livro> findByCategoriaIdIn(List<Long> categoriaIds);
}
