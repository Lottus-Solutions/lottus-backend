package br.com.lottus.edu.library.dto;

public record LivroResponseDTO(Long id, String nome, String autor, Integer quantidade, Integer quantidadeDisponivel, String categoria, String descricao) {
}
