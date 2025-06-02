package br.com.lottus.edu.library.dto;

import br.com.lottus.edu.library.model.Aluno;
import br.com.lottus.edu.library.model.Livro;

public record PerfilAlunoResponse(AlunoDTO aluno, Boolean atualIsAtrasado, String autorLivro) {
}
