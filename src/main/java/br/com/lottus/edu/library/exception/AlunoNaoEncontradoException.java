package br.com.lottus.edu.library.exception;



public class AlunoNaoEncontradoException extends RuntimeException {
    private static final String MENSAGEM_PADRAO = "Aluno não encontrado!";
    public AlunoNaoEncontradoException() {
        super(MENSAGEM_PADRAO);
    }
}
