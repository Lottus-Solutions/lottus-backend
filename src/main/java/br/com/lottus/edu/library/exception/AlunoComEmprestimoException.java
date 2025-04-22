package br.com.lottus.edu.library.exception;

public class AlunoComEmprestimoException extends RuntimeException {
    private static final String MENSAGEM_PADRAO = "Aluno já possui um empréstimo ativo!";

    public AlunoComEmprestimoException() {
        super(MENSAGEM_PADRAO);
    }
}
