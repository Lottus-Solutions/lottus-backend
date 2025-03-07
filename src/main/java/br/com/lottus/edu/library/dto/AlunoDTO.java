package br.com.lottus.edu.library.dto;

public class AlunoDTO {
    private String matricula;
    private String nome;
    private Double qtd_bonus;
    private Long turma_id;
    private Integer qtd_livros_lidos;

    public Integer getQtd_livros_lidos() {
        return qtd_livros_lidos;
    }

    public void setQtd_livros_lidos(Integer qtd_livros_lidos) {
        this.qtd_livros_lidos = qtd_livros_lidos;
    }

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Double getQtd_bonus() {
        return qtd_bonus;
    }

    public void setQtd_bonus(Double qtd_bonus) {
        this.qtd_bonus = qtd_bonus;
    }

    public Long getTurma_id() {
        return turma_id;
    }

    public void setTurma_id(Long turma_id) {
        this.turma_id = turma_id;
    }
}
