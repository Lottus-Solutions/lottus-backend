package br.com.lottus.edu.library.dto;

import br.com.lottus.edu.library.model.Aluno;
import br.com.lottus.edu.library.model.Livro;

import java.time.LocalDate;

public record PerfilAlunoResponse(AlunoDTO aluno, Boolean atualIsAtrasado, Livro livro, LocalDate dataDevolucao) {
}
