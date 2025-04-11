package br.com.lottus.edu.library.service;

import br.com.lottus.edu.library.model.Usuario;

public interface UsuarioService {

    /**
     * Cadastra um novo usuário no sistema.
     * 
     * @param usuario O objeto Usuario a ser cadastrado
     * @return O Usuario cadastrado
     */
    Usuario cadastrarUsuario(Usuario usuario);

    /**
     * Remove a conta de um usuário do sistema.
     * 
     * @param usuario O objeto Usuario a ser removido
     * @return true se a remoção for bem-sucedida, false caso contrário
     */
    Boolean removerConta(Usuario usuario);

    /**
     * Realiza o login de um usuário.
     *
     * @param email O email do usuário
     * @param senha A senha do usuário
     * @return true se o login for bem-sucedido, false caso contrário
     */
    Usuario login(String email, String senha);


    /**
     * Busca um usuário pelo email dele
     * @param email O email do usuário
     * @return O usuario encontrado, sem sua senha.
     * */
    Usuario buscarPorEmail(String email);
}