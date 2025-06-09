package br.com.lottus.edu.library.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record LivroRequestDTO(

        @NotBlank(message = "O nome não pode estar em branco")
        String nome,

        @NotBlank(message = "O autor não pode estar em branco")
        String autor,

        @NotNull(message = "A quantidade é obrigatória")
        @Min(value = 1, message = "A quantidade deve ser pelo menos 1")
        Integer quantidade,

        @NotNull(message = "A categoria é obrigatória")
        Long categoriaId,

        @NotBlank(message = "A descrição não pode estar em branco")
        String descricao
) {}
