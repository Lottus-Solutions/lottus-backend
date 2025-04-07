package br.com.lottus.edu.library.dto;

import br.com.lottus.edu.library.model.StatusEmprestimo;

import java.time.LocalDate;

public record EmprestimoDTO(Long id, String fk_aluno, String fk_livro, LocalDate dataEmprestimo,
                            LocalDate dataDevolucaoPrevista, int diasAtrasados,
                            StatusEmprestimo statusEmprestiomo) {
}
