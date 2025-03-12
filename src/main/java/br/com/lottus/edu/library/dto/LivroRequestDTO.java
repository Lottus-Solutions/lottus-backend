package br.com.lottus.edu.library.dto;

public record LivroRequestDTO(String nome, String autor, Integer quantidade, Boolean status, Long categoriaId) {
}
