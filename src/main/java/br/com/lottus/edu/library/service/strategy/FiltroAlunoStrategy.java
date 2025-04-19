package br.com.lottus.edu.library.service.strategy;

import br.com.lottus.edu.library.model.Aluno;
import br.com.lottus.edu.library.model.Emprestimo;

import java.util.List;
import java.util.stream.Collectors;

public class FiltroAlunoStrategy implements EmprestimoFiltroStrategy{
    private final Aluno aluno;

    public FiltroAlunoStrategy(Aluno aluno) {
        this.aluno = aluno;
    }


    @Override
    public List<Emprestimo> filtrar(List<Emprestimo> emprestimos) {

        return emprestimos.stream()
                .filter(e -> e.getAluno() != null && e.getAluno().getMatricula().equals(aluno.getMatricula()))
                .collect(Collectors.toList());
    }
}
