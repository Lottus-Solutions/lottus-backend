package br.com.lottus.edu.library.repository;

import br.com.lottus.edu.library.dto.EmprestimoResponseDTO;
import br.com.lottus.edu.library.model.Aluno;
import br.com.lottus.edu.library.model.Emprestimo;
import br.com.lottus.edu.library.model.Livro;
import br.com.lottus.edu.library.model.StatusEmprestimo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmprestimoRepository extends JpaRepository<Emprestimo, Long> {
    Page<Emprestimo> findByStatusEmprestimo(StatusEmprestimo statusEmprestimo, Pageable pageable);
    List<Emprestimo> findByStatusEmprestimo(StatusEmprestimo statusEmprestimo);
    List<Emprestimo> findByLivroAndAluno(Livro livro, Aluno aluno);
    List<Emprestimo> findByLivro(Livro livro);
    List<Emprestimo> findAllByAluno(Aluno aluno);
    Optional<Emprestimo> findByAluno(Aluno aluno);

    @Query("SELECT e FROM Emprestimo e " +
            "WHERE (:busca IS NULL OR :busca = '' OR " +
            "      LOWER(e.aluno.nome) LIKE LOWER(CONCAT('%', :busca, '%')) OR " +
            "      LOWER(e.livro.nome) LIKE LOWER(CONCAT('%', :busca, '%'))) " +
            "AND (:apenasAtrasado = FALSE OR e.status = 'ATRASADO') " +
            "AND (e.status IN :statusList)")
    Page<EmprestimoResponseDTO> findByBuscaOuFiltro(
            @Param("busca") String busca,
            @Param("apenasAtrasado") boolean apenasAtrasado,
            @Param("statusList") List<StatusEmprestimo> statusList,
            Pageable pageable);

    @Query("SELECT e FROM Emprestimo e WHERE e.statusEmprestimo IN (:statusList)")
    Page<Emprestimo> findByStatusIn(@Param("statusList") List<StatusEmprestimo> statusList, Pageable pageable);

    @Query("SELECT e FROM Emprestimo e WHERE " +
            "(LOWER(e.aluno.nome) LIKE LOWER(CONCAT('%', :busca, '%')) OR " +
            "LOWER(e.livro.nome) LIKE LOWER(CONCAT('%', :busca, '%'))) AND " +
            "e.statusEmprestimo IN (:statusList)")
    Page<Emprestimo> findByAlunoOrLivro(@Param("statusList") List<StatusEmprestimo> statusList, @Param("busca") String busca, Pageable pageable);

    @Query("SELECT e FROM Emprestimo e WHERE " +
            "(LOWER(e.aluno.nome) LIKE LOWER(CONCAT('%', :busca, '%')) OR " +
            "LOWER(e.livro.nome) LIKE LOWER(CONCAT('%', :busca, '%'))) " +
            "AND e.statusEmprestimo = :status")
    Page<Emprestimo> findByAlunoOrLivroAndStatus(@Param("busca") String busca, @Param("status") StatusEmprestimo status, Pageable pageable);

    @Query("SELECT e FROM Emprestimo e WHERE " +
            "e.statusEmprestimo = br.com.lottus.edu.library.model.StatusEmprestimo.ATRASADO AND " + // Filtra por status ATRASADO
            "(LOWER(e.aluno.nome) LIKE LOWER(CONCAT('%', :valor, '%')) OR " +
            "LOWER(e.livro.nome) LIKE LOWER(CONCAT('%', :valor, '%')))")
    List<Emprestimo> findAtrasadosByAlunoNomeOrLivroNomeContainingIgnoreCase(String valor);


}
