package br.com.lottus.edu.library.service.strategy;

import br.com.lottus.edu.library.model.Emprestimo;
import br.com.lottus.edu.library.model.StatusEmprestimo;

import java.util.List;
import java.util.stream.Collectors;

public class FiltroAtrasadoStrategy implements EmprestimoFiltroStrategy{



    @Override
    public List<Emprestimo> filtrar(List<Emprestimo> emprestimos) {
        return emprestimos.stream()
                .filter(e -> e.getStatusEmprestimo() == StatusEmprestimo.ATRASADO)
                .collect(Collectors.toList());
    }
}
