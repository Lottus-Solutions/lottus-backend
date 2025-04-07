package br.com.lottus.edu.library.service;

import br.com.lottus.edu.library.dto.AlunoDTO;
import br.com.lottus.edu.library.dto.EmprestimoDTO;
import br.com.lottus.edu.library.model.Aluno;
import br.com.lottus.edu.library.model.Emprestimo;
import br.com.lottus.edu.library.model.Livro;
import br.com.lottus.edu.library.model.StatusEmprestimo;
import br.com.lottus.edu.library.repository.AlunoRepository;
import br.com.lottus.edu.library.repository.EmprestimoRepository;
import br.com.lottus.edu.library.repository.LivroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class EmprestimoServiceImpl implements EmprestimoService{

    private Integer qtdDias = 15;
    private Boolean apenasAtrasados = false;

    public Integer getEspacoDias() {
        return qtdDias;
    }

    public void setEspacoDias(Integer espacoDias) {
        this.qtdDias = espacoDias;
    }

    @Autowired
    private EmprestimoRepository emprestimoRepository;

    @Autowired
    private AlunoRepository alunoRepository;

    @Autowired
    private LivroRepository livroRepository;

    @Override
    public List<Emprestimo> listarEmprestimos() {
        return emprestimoRepository.findAll();
    }

    @Override
    public Boolean renovarEmprestimo(Long idEmprestimo) {
        Optional<Emprestimo> emprestimoAAtualizar = emprestimoRepository.findById(idEmprestimo);

        if(emprestimoAAtualizar.isEmpty()){
            return false;
        }else{
            Emprestimo emprestimo = emprestimoAAtualizar.get();
            emprestimo.setDataDevolucaoPrevista(emprestimo.getDataDevolucaoPrevista().plusDays(qtdDias));
            emprestimoRepository.save(emprestimo);
            return true;
        }
    }



    @Override
    public Boolean fazerEmprestimo(Long idLivro, String matriculaAluno, LocalDate dtEmprestimo) {
        Optional<Aluno> aluno = alunoRepository.findByMatricula(matriculaAluno);
        Optional<Livro> livro = livroRepository.findById(idLivro);

        if(aluno.isPresent() && livro.isPresent()){
            Emprestimo novoEmprestimo = new Emprestimo();
            novoEmprestimo.setAluno(aluno.get());
            novoEmprestimo.setLivro(livro.get());
            novoEmprestimo.setDataEmprestimo(dtEmprestimo);
            novoEmprestimo.setDataDevolucaoPrevista(dtEmprestimo.plusDays(qtdDias));
            novoEmprestimo.setStatusEmprestimo(StatusEmprestimo.ATIVO);
            emprestimoRepository.save(novoEmprestimo);
            return true;
        }
        return false;
    }

    @Override
    public Boolean finalizarEmprestimo(Long emprestimoId) {
        Optional<Emprestimo> emprestimoOpt = emprestimoRepository.findById(emprestimoId);

        if(emprestimoOpt.isEmpty()){
            return false;
        }

        Emprestimo emprestimo = emprestimoOpt.get();

        emprestimo.setStatusEmprestimo(StatusEmprestimo.FINALIZADO);

        return true;
    }

    @Override
    public List<Emprestimo> buscarEmprestimos(Long livroId, String matricula) {

        List<Emprestimo> resultado = new ArrayList<>();


        Optional<Livro> livroOpt = livroId != null ? livroRepository.findById(livroId) : Optional.empty();
        Optional<Aluno> alunoOpt = matricula != null ? alunoRepository.findByMatricula(matricula) : Optional.empty();


        if (apenasAtrasados) {
            List<Emprestimo> listaAtrasados = emprestimoRepository.findAllByStatusEmprestimo(StatusEmprestimo.ATRASADO);


            if (livroOpt.isPresent() && alunoOpt.isPresent()) {
                // Filtra por ambos
                Livro livro = livroOpt.get();
                Aluno aluno = alunoOpt.get();
                return listaAtrasados.stream()
                        .filter(e -> e.getLivro().getId().equals(livro.getId()) &&
                                e.getAluno().getMatricula().equals(aluno.getMatricula()))
                        .collect(Collectors.toList());
            } else if (livroOpt.isPresent()) {

                Livro livro = livroOpt.get();
                return listaAtrasados.stream()
                        .filter(e -> e.getLivro().getId().equals(livro.getId()))
                        .collect(Collectors.toList());
            } else if (alunoOpt.isPresent()) {

                Aluno aluno = alunoOpt.get();
                return listaAtrasados.stream()
                        .filter(e -> e.getAluno().getMatricula().equals(aluno.getMatricula()))
                        .collect(Collectors.toList());
            } else {

                return listaAtrasados;
            }
        } else {

            if (livroOpt.isPresent() && alunoOpt.isPresent()) {

                Livro livro = livroOpt.get();
                Aluno aluno = alunoOpt.get();
                return emprestimoRepository.findByLivroAndAluno(livro, aluno);
            } else if (livroOpt.isPresent()) {

                return emprestimoRepository.findByLivro(livroOpt.get());
            } else if (alunoOpt.isPresent()) {

                return emprestimoRepository.findByAluno(alunoOpt.get());
            } else {

                return emprestimoRepository.findAll();
            }
        }
    }

    @Override
    public List<Emprestimo> buscarHistoricoLivro(Long idLivro) {
        return List.of();
    }

    @Override
    public List<Emprestimo> buscarHistoricoAluno(Long alunoId) {
        return List.of();
    }

    @Override
    public List<Emprestimo> filtrarEmprestimosAtrasados() {
        return List.of();
    }


}
