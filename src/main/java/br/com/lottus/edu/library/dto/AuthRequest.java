package br.com.lottus.edu.library.dto;

import lombok.Data;

@Data
public class AuthRequest {
    private String email;
    private String senha;
}
