package br.com.lottus.edu.library.repository;

import br.com.lottus.edu.library.model.Aluno;
import br.com.lottus.edu.library.model.Turma;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AlunoRepository extends JpaRepository<Aluno, String> {

    Optional<Aluno> findByMatricula(String matricula);

    List<Aluno> findAllByTurma(Optional<Turma> turma);
}
