package br.com.lottus.edu.library.exception;

public class TokenRefreshException extends RuntimeException {
    private static final long serialVersionUID = 1L;


    public TokenRefreshException(String token, String message) {
        super(String.format("Falha para o token [%s]: %s", token, message));
    }

    public TokenRefreshException(String message){
        super(message);
    }
}
