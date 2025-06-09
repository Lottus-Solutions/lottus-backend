package br.com.lottus.edu.library.exception;

public class TurmaNaoEncontradaException extends RuntimeException {
    public TurmaNaoEncontradaException() {
        super("Turma não encontrada!");
    }
}
