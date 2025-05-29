package br.com.lottus.edu.library.dto;

public record AlunoDTO (
        Long matricula,
        String nome,
        Double qtdBonus,
        Long turmaId,
        Integer qtdLivrosLidos,
        String livroAtual
) {}
