package br.com.lottus.edu.library.service;

import br.com.lottus.edu.library.dto.AlunoDTO;
import br.com.lottus.edu.library.exception.NenhumAlunoEncotradoException;
import br.com.lottus.edu.library.exception.AlunoNaoEncontradoException;
import br.com.lottus.edu.library.exception.TurmaNaoEncontradaException;
import br.com.lottus.edu.library.model.Aluno;
import br.com.lottus.edu.library.model.Emprestimo;
import br.com.lottus.edu.library.model.StatusEmprestimo;
import br.com.lottus.edu.library.model.Turma;
import br.com.lottus.edu.library.repository.AlunoRepository;
import br.com.lottus.edu.library.repository.EmprestimoRepository;
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

    @Autowired
    private EmprestimoRepository emprestimoRepository;

    public Aluno adicionarAluno(AlunoDTO alunodto) {
        Turma turma = turmaRepository.findById(alunodto.turmaId())
                .orElseThrow(()-> new RuntimeException("Turma do aluno não encontrada"));

        Aluno aluno = new Aluno();
        aluno.setMatricula(alunodto.matricula());
        aluno.setNome(alunodto.nome());
        aluno.setQtdBonus(alunodto.qtdBonus());
        aluno.setQtdLivrosLidos(alunodto.qtdLivrosLidos());
        aluno.setTurma(turmaRepository.getReferenceById(alunodto.turmaId()));

        return alunoRepository.save(aluno);
    }

    public Boolean removerAluno(Aluno aluno){
        alunoRepository.delete(aluno);
        return true;
    }

    public Boolean editarAluno(Long matricula, AlunoDTO alunodto) {
        // Busca o aluno existente pelo ID
        Aluno alunoExistente = alunoRepository.findByMatricula(matricula)
                .orElseThrow(AlunoNaoEncontradoException::new);

        if(alunodto.nome() != null){
            alunoExistente.setNome(alunodto.nome());
        }

        if(alunodto.qtdBonus() != null){
            alunoExistente.setQtdBonus(alunodto.qtdBonus());
        }

        if(alunodto.qtdLivrosLidos() != null){
            alunoExistente.setQtdLivrosLidos(alunodto.qtdLivrosLidos());
        }

        if (alunodto.turmaId() != null) {
            Turma turma = turmaRepository.findById(alunodto.turmaId())
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
    public AlunoDTO buscarAlunoPorMatricula(Long matricula) {
        Aluno aluno = alunoRepository.findByMatricula(matricula)
                .orElseThrow(AlunoNaoEncontradoException::new);

        return converterParaDTO(aluno);
    }

    @Override
    public List<AlunoDTO> listarAlunos() {
        List<Aluno> alunos = alunoRepository.findAll();

        if (alunos.isEmpty()) {
            throw new NenhumAlunoEncotradoException();
        }

        return alunos.stream()
                .map(this::converterParaDTO)
                .toList();
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
            aluno.resetarBonus();
            alunoRepository.save(aluno);
        }
    }

    public void resetarLivrosLidos() {
        List<Aluno> alunos = alunoRepository.findAll();

        for (Aluno aluno : alunos) {
            aluno.resetarLivrosLidos();
            alunoRepository.save(aluno);
        }
    }

    public List<AlunoDTO> listarAlunosPorNome(String nome) {
        List<Aluno> alunos = alunoRepository.findAllByNomeContainingIgnoreCase(nome);

        if (alunos.isEmpty()) {
            throw new NenhumAlunoEncotradoException();
        }

        return alunos.stream()
                .map(this::converterParaDTO)
                .toList();
    }

    @Override
    public List<AlunoDTO> buscarAlunosPorNomeETurma(String nome, Long idTurma) {
        return alunoRepository.findByNomeContainingAndTurmaId(nome, idTurma)
                .stream()
                .map(this::converterParaDTO)
                .toList();
    }

    public List<Turma> listarTurmas(){
        return turmaRepository.findAll();
    }

    private String buscarLivroAtual(Long matricula) {
        List<StatusEmprestimo> status = List.of(StatusEmprestimo.ATIVO, StatusEmprestimo.ATRASADO);

        return emprestimoRepository.findFirstByAluno_MatriculaAndStatusEmprestimoInOrderByDataEmprestimoDesc(matricula, status)
                .map(emprestimo -> emprestimo.getLivro().getNome())
                .orElse(null);
    }

    private AlunoDTO converterParaDTO(Aluno aluno) {
        String livroAtual = buscarLivroAtual(aluno.getMatricula());

        return new AlunoDTO(
                aluno.getMatricula(),
                aluno.getNome(),
                aluno.getQtdBonus(),
                aluno.getTurma().getId(),
                aluno.getQtdLivrosLidos(),
                livroAtual
        );
    }

}


