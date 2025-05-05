package br.com.lottus.edu.library.service.strategy;

import br.com.lottus.edu.library.model.Emprestimo;

import java.util.List;

public interface EmprestimoFiltroStrategy {
    List<Emprestimo> filtrar(List<Emprestimo> emprestimos);
}
