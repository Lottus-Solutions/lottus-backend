package br.com.lottus.edu.library.repository;

import br.com.lottus.edu.library.model.Aluno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AlunoRepository extends JpaRepository<Aluno, String> {

    Optional<Aluno> findByMatricula(String matricula);
}
