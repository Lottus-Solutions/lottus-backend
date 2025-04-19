package br.com.lottus.edu.library.service.strategy;

import br.com.lottus.edu.library.model.Aluno;
import br.com.lottus.edu.library.model.Livro;
import br.com.lottus.edu.library.repository.EmprestimoRepository;

import java.util.ArrayList;
import java.util.List;

public class EmprestimoFiltroStrategyFactory {

    //Pattern ChainofResponsability

    public static EmprestimoFiltroStrategy criarEstrategia(Livro livro, Aluno aluno, boolean apenasAtrasados) {
        FiltroCompostoStrategy filtroComposto = new FiltroCompostoStrategy();
        boolean algumFiltroAdicionado = false;

        if (livro != null) {
            filtroComposto.adicionarEstrategia(new FiltroLivroStrategy(livro));
            algumFiltroAdicionado = true;
        }

        if (aluno != null) {
            filtroComposto.adicionarEstrategia(new FiltroAlunoStrategy(aluno));
            algumFiltroAdicionado = true;
        }

        if (apenasAtrasados) {
            filtroComposto.adicionarEstrategia(new FiltroAtrasadoStrategy());
            algumFiltroAdicionado = true;
        }

        if (!algumFiltroAdicionado) {
            return new SemFiltroStrategy();
        }

        return filtroComposto;
    }

}
