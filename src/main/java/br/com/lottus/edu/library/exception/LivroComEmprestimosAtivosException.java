package br.com.lottus.edu.library.exception;

public class LivroComEmprestimosAtivosException extends RuntimeException {
    public LivroComEmprestimosAtivosException() {
        super("Este livro contém empréstimos ativos!");
    }
}
