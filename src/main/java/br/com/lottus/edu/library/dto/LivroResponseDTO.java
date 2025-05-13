package br.com.lottus.edu.library.dto;

public record LivroResponseDTO(Long id, String nome, String autor, Integer quantidade, Boolean status, String categoria, String descricao) {
}
