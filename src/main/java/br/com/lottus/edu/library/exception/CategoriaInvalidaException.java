package br.com.lottus.edu.library.exception;

public class CategoriaInvalidaException extends RuntimeException {
    public CategoriaInvalidaException() {
        super("Categoria invalida!");
    }
}
