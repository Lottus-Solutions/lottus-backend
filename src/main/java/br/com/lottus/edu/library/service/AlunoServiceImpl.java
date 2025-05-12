package br.com.lottus.edu.library.service;

import br.com.lottus.edu.library.dto.AlunoDTO;
import br.com.lottus.edu.library.exception.NenhumAlunoEncotradoException;
import br.com.lottus.edu.library.exception.AlunoNaoEncontradoException;
import br.com.lottus.edu.library.exception.TurmaNaoEncontradaException;
import br.com.lottus.edu.library.model.Aluno;
import br.com.lottus.edu.library.model.Turma;
import br.com.lottus.edu.library.repository.AlunoRepository;
import br.com.lottus.edu.library.repository.TurmaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AlunoServiceImpl implements AlunoService{
    @Autowired
    private AlunoRepository alunoRepository;

    @Autowired
    private TurmaRepository turmaRepository;

    public Aluno adicionarAluno(AlunoDTO alunodto) {
        Turma turma = turmaRepository.findById(alunodto.getTurma_id())
                .orElseThrow(()-> new RuntimeException("Turma do aluno não encontrada"));

        Aluno aluno = new Aluno();
        aluno.setMatricula(alunodto.getMatricula());
        aluno.setNome(alunodto.getNome());
        aluno.setQtdBonus(alunodto.getQtd_bonus());
        aluno.setQtdLivrosLidos(alunodto.getQtd_livros_lidos());
        aluno.setTurma(turmaRepository.getReferenceById(alunodto.getTurma_id()));

        return alunoRepository.save(aluno);
    }

    public Boolean removerAluno(Aluno aluno){
        alunoRepository.delete(aluno);
        return true;
    }

    public Boolean editarAluno(String matricula, AlunoDTO alunodto) {
        // Busca o aluno existente pelo ID
        Aluno alunoExistente = alunoRepository.findByMatricula(matricula)
                .orElseThrow(AlunoNaoEncontradoException::new);

        if(alunodto.getNome() != null){
            alunoExistente.setNome(alunodto.getNome());
        }

        if(alunodto.getQtd_bonus() != null){
            alunoExistente.setQtdBonus(alunodto.getQtd_bonus());
        }

        if(alunodto.getQtd_livros_lidos() != null){
            alunoExistente.setQtdLivrosLidos(alunodto.getQtd_livros_lidos());
        }

        if (alunodto.getTurma_id() != null) {
            Turma turma = turmaRepository.findById(alunodto.getTurma_id())
                    .orElseThrow(TurmaNaoEncontradaException::new);
            alunoExistente.setTurma(turma);
        }

        // Salva o aluno, fazendo o update no banco de dados
        alunoRepository.save(alunoExistente);
        return true;
    }

    @Override
    public List<Aluno> listarAlunosPorTurma(Long id) {
        Optional<Turma> turma = turmaRepository.findById(id);

        return alunoRepository.findAllByTurma(turma);
    }

    @Override
    public Optional<Aluno> buscarAlunoPorMatricula(String matricula) {
        return alunoRepository.findByMatricula(matricula);
    }

    @Override
    public List<Aluno> listarAlunos() {
        List<Aluno> alunos = alunoRepository.findAll();

        if (alunos.isEmpty()) {
            throw new NenhumAlunoEncotradoException();
        }

        return alunos;
    }

    public void atualizarPontuacao(Aluno aluno) {
        aluno.setQtdBonus(aluno.getQtdBonus() + 0.25);
        alunoRepository.save(aluno);
    }

    public void atualizarLivrosLidos(Aluno aluno) {
        aluno.setQtdLivrosLidos(aluno.getQtdLivrosLidos() + 1);
        alunoRepository.save(aluno);
    }

    public void resetarBonus() {
        List<Aluno> alunos = alunoRepository.findAll();

        for (Aluno aluno : alunos) {
            aluno.setQtdBonus(0.0);
            alunoRepository.save(aluno);
        }
    }

    public void resetarLivrosLidos() {
        List<Aluno> alunos = alunoRepository.findAll();

        for (Aluno aluno : alunos) {
            aluno.setQtdLivrosLidos(0);
            alunoRepository.save(aluno);
        }
    }
}
