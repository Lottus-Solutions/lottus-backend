package br.com.lottus.edu.library.service;
import br.com.lottus.edu.library.dto.RequestSolicitarResetSenha;
import br.com.lottus.edu.library.dto.ResponseSolicitarReset;
import br.com.lottus.edu.library.dto.UsuarioDTO;
import br.com.lottus.edu.library.model.Usuario;
import com.sendgrid.Response;

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

    /**
     * Renova o semestre da base de dados.
     *
     *
     */
    void renovarSemestre();


    /**
     * Soliciar o reset de senha do usuário
     *
     * @param email o email do usuário
     * @return dto de resposta com link para reset de senha e token para validação
     */
    ResponseSolicitarReset solicitarResetSenha(String email);

    /**
     * Reseta a senha do usuário
     *
     * @param token o token UUID gerado para o reset
     * @param novaSenha a nova senha do usuário
     * @return true se o reset for bem-sucedido, false caso contrário
     */
    Boolean resetarSenha(String token, String novaSenha);

    UsuarioDTO editarUsuario(Long id, Usuario usuario);
}