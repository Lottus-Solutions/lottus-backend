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


   //TODO corrigir o nome do método para algo mais claro
   //TODO será necessario docker compose com script de inicialização do banco para crição de indexes funcionais
   @Query("SELECT new br.com.lottus.edu.library.dto.LivroResponseDTO(" +
           "  l.id, l.nome, l.autor, l.quantidade, l.quantidadeDisponivel, l.status, c.nome, l.descricao) " +
           "FROM Livro l JOIN l.categoria c " +
           "WHERE (:termoBusca IS NULL OR :termoBusca = '' OR " +
           "      (l.nome LIKE CONCAT('%', :termoBusca, '%') OR " +
           "       l.autor LIKE CONCAT('%', :termoBusca, '%'))) ")
   Page<LivroResponseDTO> findByNomeContainingIgnoreCaseOrAutorContainingIgnoreCase(@Param("termoBusca")String valor, Pageable pageable);
   List<Livro> findByStatus(StatusLivro status);
   List<Livro> findByCategoriaIdIn(List<Long> categoriaIds);
}
