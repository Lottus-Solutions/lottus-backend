package br.com.lottus.edu.library.security;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomUserPrincipal {
    private String email;
    private Long userId;
} 