package br.com.lottus.edu.library.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AlunoDTO (
        Long matricula,

        @NotBlank
        @NotNull
        String nome,
        Double qtdBonus,

        @NotNull
        Long turmaId,
        Integer qtdLivrosLidos,
        String livroAtual
) {}
