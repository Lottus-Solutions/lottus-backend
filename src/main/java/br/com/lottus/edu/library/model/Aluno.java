package br.com.lottus.edu.library.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "aluno")
public class Aluno {

    @Id
    @Column(nullable = false, unique = true)
    private String matricula;

    private String nome;
    private Double qtdBonus;
    private Integer qtdLivrosLidos;

    @ManyToOne
    @JoinColumn(name = "fk_turma", nullable = false)
    private Turma turma;

    @OneToMany(mappedBy = "aluno", cascade = CascadeType.REMOVE, orphanRemoval = true)
    @JsonManagedReference
    @JsonIgnore
    private List<Emprestimo> emprestimos = new ArrayList<>();

    public void resetarBonus() {
        this.qtdBonus = 0.0;
    }

    public void resetarLivrosLidos() {
        this.qtdLivrosLidos = 0;
    }

    public Boolean podeFazerEmprestimo() {
        return emprestimos.stream()
                .noneMatch(emprestimo -> emprestimo.getStatusEmprestimo() == StatusEmprestimo.ATIVO);
    }

    public Integer getQtdLivrosLidos() {
        return qtdLivrosLidos;
    }

    public void setQtdLivrosLidos(Integer qtdLivrosLidos) {
        this.qtdLivrosLidos = qtdLivrosLidos;
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

    public Double getQtdBonus() {
        return qtdBonus;
    }

    public void setQtdBonus(Double qtdBonus) {
        this.qtdBonus = qtdBonus;
    }

    public Turma getTurma() {
        return turma;
    }

    public void setTurma(Turma turma) {
        this.turma = turma;
    }

    public List<Emprestimo> getEmprestimos() {
        return emprestimos;
    }

    @Override
    public String toString() {
        return "Aluno{" +
                "matricula='" + matricula + '\'' +
                ", nome='" + nome + '\'' +
                ", qtdBonus=" + qtdBonus +
                ", qtdLivrosLidos=" + qtdLivrosLidos +
                ", turma=" + turma +
                '}';
    }
}

