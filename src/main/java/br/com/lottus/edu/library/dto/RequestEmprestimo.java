package br.com.lottus.edu.library.dto;

import br.com.lottus.edu.library.model.StatusEmprestimo;

import java.time.LocalDate;

public record RequestEmprestimo(Long id, String matriculaAluno, Long fk_livro, LocalDate dataEmprestimo) {
}
