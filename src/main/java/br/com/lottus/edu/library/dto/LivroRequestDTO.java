package br.com.lottus.edu.library.dto;

public record LivroRequestDTO(String nome, String autor, Integer quantidade, Long categoriaId, String descricao) {
}
