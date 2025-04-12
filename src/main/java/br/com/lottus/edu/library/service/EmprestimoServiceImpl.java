package br.com.lottus.edu.library.service;

import br.com.lottus.edu.library.dto.RequestEmprestimo;
import br.com.lottus.edu.library.model.Aluno;
import br.com.lottus.edu.library.model.Emprestimo;
import br.com.lottus.edu.library.model.Livro;
import br.com.lottus.edu.library.model.StatusEmprestimo;
import br.com.lottus.edu.library.repository.AlunoRepository;
import br.com.lottus.edu.library.repository.EmprestimoRepository;
import br.com.lottus.edu.library.repository.LivroRepository;
import br.com.lottus.edu.library.utils.LimitedList;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Getter @Setter
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
    public Optional<Emprestimo> fazerEmprestimo(RequestEmprestimo requestEmprestimo) {
        Optional<Aluno> aluno = alunoRepository.findByMatricula(requestEmprestimo.matriculaAluno());
        Optional<Livro> livro = livroRepository.findById(requestEmprestimo.fk_livro());

        Emprestimo novoEmprestimo = new Emprestimo();

        if(aluno.isPresent() && livro.isPresent()){

            novoEmprestimo.setAluno(aluno.get());
            novoEmprestimo.setLivro(livro.get());
            novoEmprestimo.setDataEmprestimo(requestEmprestimo.dataEmprestimo());
            novoEmprestimo.setDataDevolucaoPrevista(requestEmprestimo.dataEmprestimo().plusDays(qtdDias));
            novoEmprestimo.setStatusEmprestimo(StatusEmprestimo.ATIVO);
        }

            return Optional.of(emprestimoRepository.save(novoEmprestimo));

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
        Livro livro = livroRepository.findById(idLivro)
                .orElseThrow(() -> new RuntimeException("Livro não encontrado"));

        List<Emprestimo> listaFinalizados =  emprestimoRepository
                .findAllByStatusEmprestimo(StatusEmprestimo.FINALIZADO);

        List<Emprestimo> historicoLivro = new LimitedList<Emprestimo>(7);


        listaFinalizados.stream()
                .filter(e -> Objects.equals(e.getLivro().getId(), livro.getId()))
                .forEach(e ->{
                    try{
                        historicoLivro.add(e);
                    }catch(IllegalStateException ex){
                        System.out.println("Exceção capturada" + ex.getMessage());
                    }
                });

//                if(!listaFinalizados.isEmpty()){
//
//                    listaFinalizados.forEach(emprestimo -> {
//
//                    Long livroId = emprestimo.getLivro().getId();
//
//                    if(Objects.equals(livroId, livro.getId())){
//
//                        try{
//                            historicoLivro.add(emprestimo);
//                        }catch(IllegalStateException e){
//                            System.out.println("Exceção capturada: " + e.getMessage());
//                        }
//                    }
//                    });
//                }

                return historicoLivro;

    }

    @Override
    public List<Emprestimo> buscarHistoricoAluno(String matricula) {
        Aluno aluno = alunoRepository.findById(matricula)
                .orElseThrow(() -> new RuntimeException("Aluno não encontrado"));

        List<Emprestimo> emprestimosFinalizados = emprestimoRepository.findAllByStatusEmprestimo(StatusEmprestimo.FINALIZADO);

        List<Emprestimo> historicoAluno = new LimitedList<Emprestimo>(7);

        emprestimosFinalizados.stream()
                .filter(emprestimo -> Objects.equals(emprestimo.getAluno().getMatricula(), aluno.getMatricula()))
                .forEach(emprestimo -> {
                    try {
                        historicoAluno.add(emprestimo);
                    }catch(IllegalStateException ex){
                        System.out.println("Exceção capturada: " + ex.getMessage());
                    }
                });

//        if(!emprestimosFinalizados.isEmpty()){
//            listarEmprestimos().forEach(emprestimo ->{
//                String matriculaAluno = emprestimo.getAluno().getMatricula();
//
//                if(Objects.equals(matricula, aluno.getMatricula())){
//
//                    try {
//                        historicoAluno.add(emprestimo);
//                    }catch(IllegalStateException e){
//                        System.out.println("Exceção capturada:" + e.getMessage());
//                    }
//                }
//            });
//        }

        return historicoAluno;
    }

    @Override
    public List<Emprestimo> filtrarEmprestimosAtrasados() {
        List<Emprestimo> emprestimosAtrasados = emprestimoRepository.findAllByStatusEmprestimo(StatusEmprestimo.ATRASADO);
        setApenasAtrasados(true);

        return emprestimosAtrasados;
    }


}
