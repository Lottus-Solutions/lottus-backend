package br.com.lottus.edu.library.repository;

import br.com.lottus.edu.library.model.Livro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LivroRepository extends JpaRepository<Livro, Long> {
    @Query("SELECT l FROM Livro l WHERE " +
            "LOWER (l.nome) LIKE LOWER(CONCAT('%', :valor, '%')) OR " +
            "LOWER (l.autor) LIKE LOWER(CONCAT('%', :valor, '%'))")
    List<Livro> findByNomeOrAutor(@Param("valor") String valor);
    List<Livro> findByCategoriaIdIn(List<Long> categoriaIds);
}
