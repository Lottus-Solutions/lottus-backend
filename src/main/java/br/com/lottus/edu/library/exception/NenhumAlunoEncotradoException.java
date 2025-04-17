package br.com.lottus.edu.library.exception;


public class NenhumAlunoEncotradoException extends RuntimeException{

    private static final String mensagem = "Nenhum aluno foi encontrado!";

    public  NenhumAlunoEncotradoException() {
        super(mensagem);
    }

}
