package br.com.lottus.edu.library.repository;

import br.com.lottus.edu.library.dto.LivroResponseDTO;
import br.com.lottus.edu.library.model.Livro;
import br.com.lottus.edu.library.model.StatusLivro;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LivroRepository extends JpaRepository<Livro, Long> {

   @Query("SELECT new br.com.lottus.edu.library.dto.LivroResponseDTO(" +
           "  l.id, l.nome, l.autor, l.quantidade, l.quantidadeDisponivel, l.status, c.nome, l.descricao) " +
           "FROM Livro l JOIN l.categoria c " +
           "WHERE (:termoBusca IS NULL OR " +
           "      LOWER(l.nome) LIKE LOWER(CONCAT('%', :termoBusca, '%')) OR " +
           "      LOWER(l.autor) LIKE LOWER(CONCAT('%', :termoBusca, '%'))) " +
           "AND (:status IS NULL OR l.status IN :status) " +
           "AND (:categoriaId IS NULL OR c.id = :categoriaId)")
   Page<LivroResponseDTO> findByBuscaOuFiltro(
           @Param("termoBusca")String valor,
           @Param("status") List<StatusLivro> status,
           @Param("categoriaId") Long categoriaId,
           Pageable pageable);
}
