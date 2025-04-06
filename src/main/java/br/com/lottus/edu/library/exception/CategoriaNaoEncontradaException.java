package br.com.lottus.edu.library.exception;

public class CategoriaNaoEncontradaException extends RuntimeException {

    private static final String MENSAGEM_PADRAO = "Categoria não encontrada!";

    public CategoriaNaoEncontradaException() {
        super(MENSAGEM_PADRAO);
    }
}
