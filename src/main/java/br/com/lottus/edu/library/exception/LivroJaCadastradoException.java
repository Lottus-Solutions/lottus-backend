package br.com.lottus.edu.library.exception;

public class LivroJaCadastradoException extends RuntimeException{
    public LivroJaCadastradoException() {
        super("Livro já cadastrado.");
    }
}
