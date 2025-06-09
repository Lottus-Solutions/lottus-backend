package br.com.lottus.edu.library.dto;

import java.time.LocalDateTime;

public class ErrorResponse {
    private int status;
    private String mensagem;
    private LocalDateTime timestamp;

    public ErrorResponse(int status, String mensagem, LocalDateTime timestamp) {
        this.status = status;
        this.mensagem = mensagem;
        this.timestamp = timestamp;
    }
}
