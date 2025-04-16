package br.com.lottus.edu.library.exception;

public class MultiClassNotFundException extends RuntimeException {

    public <E> MultiClassNotFundException(E obj, E obj2) {
        super(String.format("Objeto não encontrado: %s ou %s", obj, obj2));
        ;
    }
}
