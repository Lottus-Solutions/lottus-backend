package br.com.lottus.edu.library.service;

import br.com.lottus.edu.library.dto.AlunoDTO;
import br.com.lottus.edu.library.model.Aluno;
import br.com.lottus.edu.library.model.Turma;
import br.com.lottus.edu.library.repository.AlunoRepository;
import br.com.lottus.edu.library.repository.TurmaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        aluno.setQtdLivrosLidos(aluno.getQtdLivrosLidos());
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
                .orElseThrow(() -> new RuntimeException("Aluno não encontrado"));

        System.out.println("Aluno:" + alunodto.getNome() + alunodto.getQtd_livros_lidos());

        // Atualiza os dados do aluno
        if(alunodto.getNome() != null){
            alunoExistente.setNome(alunodto.getNome());
        }

        if(alunodto.getQtd_bonus() != null){
            alunoExistente.setQtdBonus(alunodto.getQtd_bonus());
        }

        if(alunodto.getQtd_livros_lidos() != null){
            alunoExistente.setQtdLivrosLidos(alunodto.getQtd_livros_lidos());
        }


        // Se a turma for modificada, verificamos a existência da nova turma
        if (alunodto.getTurma_id() != null) {
            Turma turma = turmaRepository.findById(alunodto.getTurma_id())
                    .orElseThrow(() -> new RuntimeException("Turma não encontrada"));
            alunoExistente.setTurma(turma);
        }

        // Salva o aluno, fazendo o update no banco de dados
        alunoRepository.save(alunoExistente);
        return true;
    }


}
