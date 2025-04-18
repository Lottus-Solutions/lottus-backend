package br.com.lottus.edu.library.service;

import br.com.lottus.edu.library.dto.RequestEmprestimo;
import br.com.lottus.edu.library.model.Aluno;
import br.com.lottus.edu.library.model.Emprestimo;
import br.com.lottus.edu.library.model.Livro;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface EmprestimoService {

    /**
     * Listar todos os empréstimos registrados no sistema.
     *
     * @return Lista de empréstimos.
     */
    List<Emprestimo> listarEmprestimos();

    /**
     * Renovar um empréstimo existente.
     *
     * @param idEmprestimo o ID do empréstimo a ser renovado.
     * @return true se a renovação for bem-sucedida, false caso contrário.
     */
    Boolean renovarEmprestimo(Long idEmprestimo);

    /**
     * Realizar um novo empréstimo de um livro para um aluno.
     *
     * @param requestEmprestimo objeto de transferencia de dados do emrpestimo.
     * @return true se o empréstimo for realizado com sucesso, false caso contrário.
     */
    Optional<Emprestimo> fazerEmprestimo(RequestEmprestimo requestEmprestimo);

    /**
     * Finalizar um empréstimo, indicando que o livro foi devolvido.
     *
     * @param emprestimoId o objeto Emprestimo a ser finalizado.
     * @return O emprestimo criado .
     */
    Boolean finalizarEmprestimo(Long emprestimoId);

    /**
     * Buscar empréstimos pelo valor informado (pode ser ID do aluno, ID do livro ou outro critério).
     *
     * @param livroId o id do livro a ser buscado.
     * @param matricula a matricula do aluno a ser buscado.
     * @return Lista de empréstimos correspondentes ao critério de busca.
     */
    List<Emprestimo> buscarEmprestimos(Long livroId, String matricula);

    /**
     * Buscar o histórico de empréstimos de um livro específico.
     *
     * @param idLivro o ID do livro.
     * @return Lista de empréstimos relacionados ao livro informado.
     */
    List<Emprestimo> buscarHistoricoLivro(Long idLivro);

    /**
     * Buscar o histórico de empréstimos de um aluno específico.
     *
     * @param matricula a matricula do aluno.
     * @return Lista de empréstimos feitos pelo aluno informado.
     */
    List<Emprestimo> buscarHistoricoAluno(String matricula);

    List<Emprestimo> filtrarEmprestimosAtrasados();
}
