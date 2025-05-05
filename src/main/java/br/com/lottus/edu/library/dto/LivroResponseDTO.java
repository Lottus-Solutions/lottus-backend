package br.com.lottus.edu.library.dto;

import br.com.lottus.edu.library.model.StatusLivro;

public record LivroResponseDTO(Long id, String nome, String autor, Integer quantidade, StatusLivro status, String categoria) {
}
