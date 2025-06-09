package br.com.lottus.edu.library.exception;

public class LivroIndisponivelException extends RuntimeException {
    public LivroIndisponivelException() {
        super("Livro indisponível para empréstimo.");
    }
}
