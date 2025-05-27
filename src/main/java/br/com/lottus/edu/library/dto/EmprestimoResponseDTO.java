package br.com.lottus.edu.library.dto;

import java.time.LocalDate;

public record EmprestimoResponseDTO(Long id, Long matriculaAluno, String nomeAluno, String turma, Long idLivro, String livro, LocalDate dataEmprestimo, LocalDate dataDevolucao, Integer diasAtraso){
}