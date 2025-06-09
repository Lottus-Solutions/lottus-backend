package br.com.lottus.edu.library.exception;

public class TurmaNaoExisteException extends RuntimeException {
    public TurmaNaoExisteException() {
        super("Esta turma não existe");
    }
}
