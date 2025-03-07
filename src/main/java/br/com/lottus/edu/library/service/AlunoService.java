package br.com.lottus.edu.library.service;

import br.com.lottus.edu.library.dto.AlunoDTO;
import br.com.lottus.edu.library.model.Aluno;

public interface AlunoService {

    /**
     *
     * Adicionar um novo aluno no sistema
     *
     * @param alunodto o objeto AlunoDTO contendo as informações do aluno a ser cadastrado
     * @return o Aluno adicionado
     */
    Aluno adicionarAluno(AlunoDTO alunodto);


    /**
     *
     * Remover um novo aluno do sistema
     *
     * @param aluno o objeto Aluno a ser removido
     * @return true se a remoção for bem sucedida, false caso contrario
     */
    Boolean removerAluno(Aluno aluno);

    /**
     *
     * Editar um aluno do sistema
     * @param matricula o numero da matricula do aluno a ser modificado
     * @param alunodto o objeto AlunoDTO com os dados atualizados para o aluno
     * @return true se a edição for bem sucedida
     */
    Boolean editarAluno(String matricula, AlunoDTO alunodto);

}
