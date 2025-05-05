package br.com.lottus.edu.library.service.strategy;

import br.com.lottus.edu.library.model.Emprestimo;
import br.com.lottus.edu.library.model.Livro;

import java.util.List;
import java.util.stream.Collectors;

public class FiltroLivroStrategy implements EmprestimoFiltroStrategy{

    private final Livro livro;

    public FiltroLivroStrategy(Livro livro) {
        this.livro = livro;
    }


    @Override
    public List<Emprestimo> filtrar(List<Emprestimo> emprestimos) {

        return emprestimos.stream()
                .filter(e -> e.getLivro() != null && e.getLivro().getId().equals(livro.getId()))
                .collect(Collectors.toList());
    }
}
