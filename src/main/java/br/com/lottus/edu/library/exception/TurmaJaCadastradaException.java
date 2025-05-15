package br.com.lottus.edu.library.exception;

public class TurmaJaCadastradaException extends RuntimeException{
    public TurmaJaCadastradaException() {
        super("Essa turma já está cadastrada.");
    }
}
