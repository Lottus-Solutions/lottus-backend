package br.com.lottus.edu.library.service;

import br.com.lottus.edu.library.dto.AlunoDTO;
import br.com.lottus.edu.library.model.Aluno;
import br.com.lottus.edu.library.model.Turma;

import java.util.List;
import java.util.Optional;

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
    Boolean editarAluno(Long matricula, AlunoDTO alunodto);

    /**
     *
     * Listar alunos por turma
     * @param turma a turma a ter os alunos listados
     * @return Lis<Alunos> lista de alunos da turma
     */
    List<Aluno> listarAlunosPorTurma(Long turma);

    /**
     * Buscar aluno por matricula
     *
     * @param matricula o numero da matriculo do aluno
     * @return aluno o objeto aluno para alimentação dos seus dados no perfil
     */
    AlunoDTO buscarAlunoPorMatricula(Long matricula);

    List<AlunoDTO> listarAlunos();

    /**
     * Listar alunos por nome
     *
     * @param nome o nome do aluno a ser buscado
     * @return lista de alunos com o nome informado
     */
    List<AlunoDTO> listarAlunosPorNome(String nome);

    /**
     * lista alunos dentro de uma turma por nome
     *
     * @return List<Aluno> lista de alunos da turma
     * @param nome o nome do aluno a ser buscado
     * @param idTurma o id da turma a ser buscada
     */
    List<AlunoDTO> buscarAlunosPorNomeETurma(String nome, Long idTurma);


    List<Turma> listarTurmas();

    void atualizarPontuacao(Aluno aluno);

    void atualizarLivrosLidos(Aluno aluno);

}
