package br.com.lottus.edu.library.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;

public record EmprestimoResponseDTO(
        Long id,
        Long matriculaAluno,
        String nomeAluno,
        String turma,
        Long idLivro,
        String livro,
        @JsonFormat(pattern = "dd/MM/yyyy")
        LocalDate dataEmprestimo,
        @JsonFormat(pattern = "dd/MM/yyyy")
        LocalDate dataDevolucao,
        Integer diasAtraso){
}