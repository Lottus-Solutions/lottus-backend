package br.com.lottus.edu.library.exception;

public class EmailJaCadastradoException extends RuntimeException {
    public EmailJaCadastradoException() {
        super("Já existe um usuário cadastrado com este email.");
    }
}
