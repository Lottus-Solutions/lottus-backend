package br.com.lottus.edu.library.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

    @Data
    public class AuthRequest {
        private String email;
        private String senha;

        private Boolean rememberMe = false;

    }
