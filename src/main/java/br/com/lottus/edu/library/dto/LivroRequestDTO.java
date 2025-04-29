package br.com.lottus.edu.library.dto;

import br.com.lottus.edu.library.model.StatusLivro;

public record LivroRequestDTO(String nome, String autor, Integer quantidade, StatusLivro status, Long categoriaId) {
}
