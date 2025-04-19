package br.com.lottus.edu.library.repository;

import br.com.lottus.edu.library.model.Aluno;
import br.com.lottus.edu.library.model.Emprestimo;
import br.com.lottus.edu.library.model.Livro;
import br.com.lottus.edu.library.model.StatusEmprestimo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmprestimoRepository extends JpaRepository<Emprestimo, Long> {
    List<Emprestimo> findAllByStatusEmprestimo(StatusEmprestimo statusEmprestimo);
    List<Emprestimo> findByLivroAndAluno(Livro livro, Aluno aluno);
    List<Emprestimo> findByLivro(Livro livro);
    List<Emprestimo> findByAluno(Aluno aluno);
}
