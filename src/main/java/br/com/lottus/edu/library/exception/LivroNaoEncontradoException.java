package br.com.lottus.edu.library.exception;

public class LivroNaoEncontradoException extends RuntimeException {

    private final static String MENSAGEM_PADRAO = "Livro não encontrado!";

    public LivroNaoEncontradoException() {
        super(MENSAGEM_PADRAO);
    }
}
