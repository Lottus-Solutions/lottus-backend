package br.com.lottus.edu.library.service.strategy;

import br.com.lottus.edu.library.model.Emprestimo;

import java.util.ArrayList;
import java.util.List;

public class FiltroCompostoStrategy implements EmprestimoFiltroStrategy{
    private final List<EmprestimoFiltroStrategy> estrategias = new ArrayList<>();

    public void adicionarEstrategia(EmprestimoFiltroStrategy estrategia){
        if(estrategia != null){
           estrategias.add(estrategia);
        }
    }

    @Override
    public List<Emprestimo> filtrar(List<Emprestimo> emprestimos) {
        List<Emprestimo> resultadoFiltrado = emprestimos;

        for (EmprestimoFiltroStrategy estrategia : estrategias) {
            resultadoFiltrado = estrategia.filtrar(resultadoFiltrado);
        }

        return resultadoFiltrado;
    }
}
