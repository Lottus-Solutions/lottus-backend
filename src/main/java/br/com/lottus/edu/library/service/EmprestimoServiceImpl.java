package br.com.lottus.edu.library.service;

import br.com.lottus.edu.library.dto.RequestEmprestimo;
import br.com.lottus.edu.library.exception.AlunoComEmprestimoException;
import br.com.lottus.edu.library.exception.AlunoNaoEncontradoException;
import br.com.lottus.edu.library.exception.EmprestimoNaoEncontradoException;
import br.com.lottus.edu.library.exception.MultiClassNotFundException;
import br.com.lottus.edu.library.model.Aluno;
import br.com.lottus.edu.library.model.Emprestimo;
import br.com.lottus.edu.library.model.Livro;
import br.com.lottus.edu.library.model.StatusEmprestimo;
import br.com.lottus.edu.library.repository.AlunoRepository;
import br.com.lottus.edu.library.repository.EmprestimoRepository;
import br.com.lottus.edu.library.repository.LivroRepository;
import br.com.lottus.edu.library.service.strategy.EmprestimoFiltroStrategy;
import br.com.lottus.edu.library.service.strategy.EmprestimoFiltroStrategyFactory;
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

    @Autowired
    private AlunoServiceImpl alunoService;

    @Override
    public List<Emprestimo> listarEmprestimos() {
        return emprestimoRepository.findAll();
    }

    @Override
    public Boolean renovarEmprestimo(Long idEmprestimo) {
        Optional<Emprestimo> emprestimoAAtualizar = emprestimoRepository.findById(idEmprestimo);

        if(emprestimoAAtualizar.isEmpty()){
            throw new EmprestimoNaoEncontradoException();
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

        List<Emprestimo> emprestimosAtivos = emprestimoRepository.findAllByStatusEmprestimo(StatusEmprestimo.ATIVO);

        emprestimosAtivos.forEach(e -> {
            if(e.getAluno().getMatricula().equals(requestEmprestimo.matriculaAluno())) {
                throw new AlunoComEmprestimoException();
            }
        });

        Emprestimo novoEmprestimo = new Emprestimo();

        if(aluno.isPresent() && livro.isPresent()){

            novoEmprestimo.setAluno(aluno.get());
            novoEmprestimo.setLivro(livro.get());
            novoEmprestimo.setDataEmprestimo(requestEmprestimo.dataEmprestimo());
            novoEmprestimo.setDataDevolucaoPrevista(requestEmprestimo.dataEmprestimo().plusDays(qtdDias));
            novoEmprestimo.setStatusEmprestimo(StatusEmprestimo.ATIVO);
        }else{
            throw  new MultiClassNotFundException(aluno, livro);
        }

            return Optional.of(emprestimoRepository.save(novoEmprestimo));

    }

    @Override
    public Boolean finalizarEmprestimo(Long emprestimoId) {
        Optional<Emprestimo> emprestimoOpt = emprestimoRepository.findById(emprestimoId);

        if(emprestimoOpt.isEmpty()){
            throw new EmprestimoNaoEncontradoException();
        }

        Emprestimo emprestimo = emprestimoOpt.get();

        emprestimo.setStatusEmprestimo(StatusEmprestimo.FINALIZADO);
        emprestimoRepository.save(emprestimo);

        alunoService.atualizarLivrosLidos(emprestimo.getAluno());

        if (emprestimo.getAluno().getQtdLivrosLidos() > 4) {
            alunoService.atualizarPontuacao(emprestimo.getAluno());
        }

        emprestimoRepository.save(emprestimo);

        return true;
    }

    @Override
    public List<Emprestimo> buscarEmprestimos(Long livroId, String matricula, Boolean apenasAtrasados) {

        Optional<Livro> livroOpt = livroId != null ? livroRepository.findById(livroId) : Optional.empty();
        Optional<Aluno> alunoOpt = matricula != null ? alunoRepository.findByMatricula(matricula) : Optional.empty();

        if(livroId != null && livroOpt.isEmpty()){
            return Collections.emptyList();
        }

        if(matricula != null && alunoOpt.isEmpty()){
            return Collections.emptyList();
        }

        List<Emprestimo> todosEmprestimos = emprestimoRepository.findAll();

        boolean filterAtrasados = apenasAtrasados != null && apenasAtrasados;

        EmprestimoFiltroStrategy estrategia = EmprestimoFiltroStrategyFactory.criarEstrategia(
                livroOpt.orElse(null),
                alunoOpt.orElse(null),
                filterAtrasados // Passa o valor booleano
        );

        return estrategia.filtrar(todosEmprestimos);
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

                return historicoLivro;

    }

    @Override
    public List<Emprestimo> buscarHistoricoAluno(String matricula) {
        Aluno aluno = alunoRepository.findById(matricula)
                .orElseThrow(AlunoNaoEncontradoException::new);

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

        return historicoAluno;
    }

    @Override
    public List<Emprestimo> filtrarEmprestimosAtrasados() {
        setApenasAtrasados(true);
        List<Emprestimo> todosEmprestimos = emprestimoRepository.findAll();
        EmprestimoFiltroStrategy estrategiaAtrasados = EmprestimoFiltroStrategyFactory.criarEstrategia(
                null, null, true
        );
        return estrategiaAtrasados.filtrar(todosEmprestimos);
    }

    public void resetarStatus() {
         List<Emprestimo> emprestimosFinalizados = emprestimoRepository.findAllByStatusEmprestimo(StatusEmprestimo.FINALIZADO);

         emprestimosFinalizados.forEach(emprestimo -> {
             emprestimo.setStatusEmprestimo(StatusEmprestimo.ARQUIVADO);
             emprestimoRepository.save(emprestimo);
         });
    }

}
