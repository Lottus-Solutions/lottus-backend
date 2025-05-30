package br.com.lottus.edu.library.exception;

public class EmprestimoAtivoException extends RuntimeException {
    public EmprestimoAtivoException() {
        super("O aluno já possui um empréstimo ativo.");
    }
}
