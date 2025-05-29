package br.com.lottus.edu.library.exception;

public class StatusInvalidoException extends RuntimeException {
    public StatusInvalidoException(String status) {
        super("Status inválido: " + status);
    }
}
