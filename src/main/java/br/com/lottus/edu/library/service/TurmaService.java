package br.com.lottus.edu.library.service;

import br.com.lottus.edu.library.exception.TurmaJaCadastradaException;
import br.com.lottus.edu.library.exception.TurmaNaoEncontradaException;
import br.com.lottus.edu.library.model.Turma;
import br.com.lottus.edu.library.repository.TurmaRepository;
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

    public void removerTurma(Long id) {
        Turma turma = turmaRepository.findById(id)
                .orElseThrow(TurmaNaoEncontradaException::new);

        turmaRepository.delete(turma);
    }



}
