package br.com.lottus.edu.library.service;

import br.com.lottus.edu.library.exception.TurmaJaCadastradaException;
import br.com.lottus.edu.library.exception.TurmaNaoEncontradaException;
import br.com.lottus.edu.library.model.Turma;
import br.com.lottus.edu.library.repository.TurmaRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TurmaService {

    public TurmaRepository turmaRepository;

    public TurmaService(TurmaRepository turmaRepository) {
        this.turmaRepository = turmaRepository;
    }

    public List<Turma> listarTurmas() {

        List<Turma> turmas = turmaRepository.findAll();

        if (turmas.isEmpty()) {
            throw new TurmaNaoEncontradaException();
        }

        return turmaRepository.findAll();
    }

    public Turma adicionarTurma(Turma turma) {
        if (turmaRepository.findBySerieIgnoreCase(turma.getSerie())) {
            throw new TurmaJaCadastradaException();
        }

        return turmaRepository.save(turma);
    }

    public ResponseEntity<Void> removerTurma(Long id) {
        Turma turma = turmaRepository.findById(id)
                .orElseThrow(TurmaNaoEncontradaException::new);

        turmaRepository.delete(turma);
        return ResponseEntity.noContent().build();
    }

    public Turma editarTurma(Long matricula, Turma turmaRequest) {
        Turma turma = turmaRepository.findById(matricula)
                .orElseThrow(TurmaNaoEncontradaException::new);

        List<Turma> turmas = turmaRepository.findAll();

        if (turmas.stream().anyMatch(t -> t.getSerie().equalsIgnoreCase(turmaRequest.getSerie()))) {
            throw new TurmaJaCadastradaException();
        }

        turma.setSerie(turmaRequest.getSerie());
        return turmaRepository.save(turma);
    }

}
