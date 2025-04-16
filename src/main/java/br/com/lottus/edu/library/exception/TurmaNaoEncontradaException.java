package br.com.lottus.edu.library.exception;

public class TurmaNaoEncontradaException extends RuntimeException {

  private static final String MENSAGEM_PADRAO = "Turma não encontrada";


    public TurmaNaoEncontradaException() {
        super(MENSAGEM_PADRAO);
    }
}
