package br.com.lottus.edu.library.exception;

public class EmprestimoNaoEncontradoException extends RuntimeException {

    private final static String MENSAGEM_PADRAO = "Livro não encontrado!";

    public EmprestimoNaoEncontradoException() {
        super(MENSAGEM_PADRAO);
    }
}
