package br.com.lottus.edu.library.exception;

public class CategoriaJaExistenteException extends RuntimeException {

    private static final String mensagem = "A categoria já existe!";

    public CategoriaJaExistenteException() {
        super(mensagem);
    }
}
