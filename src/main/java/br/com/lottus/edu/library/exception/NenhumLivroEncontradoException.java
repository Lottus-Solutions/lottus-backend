package br.com.lottus.edu.library.exception;

public class NenhumLivroEncontradoException extends RuntimeException {

    private static final String mensagem = "Nenhum livro foi encontrado.";

    public NenhumLivroEncontradoException() {
        super(mensagem);
    }

}
