package br.com.lottus.edu.library.service;

import br.com.lottus.edu.library.dto.EmprestimoResponseDTO;
import br.com.lottus.edu.library.dto.RequestEmprestimo;
import br.com.lottus.edu.library.exception.*;
import br.com.lottus.edu.library.model.*;
import br.com.lottus.edu.library.repository.AlunoRepository;
import br.com.lottus.edu.library.repository.EmprestimoRepository;
import br.com.lottus.edu.library.repository.LivroRepository;
import br.com.lottus.edu.library.utils.LimitedList;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

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
    private AlunoService alunoService;

    @Override
    public Page<EmprestimoResponseDTO> listarEmprestimos(String busca, boolean atrasados, Pageable pageable) {
        List<StatusEmprestimo> statusList = Arrays.asList(StatusEmprestimo.ATIVO, StatusEmprestimo.ATRASADO);
        return emprestimoRepository.findByBuscaOuFiltro(busca, atrasados, statusList, pageable)
                .map(emprestimo -> new EmprestimoResponseDTO(
                        emprestimo.getId(),
                        emprestimo.getAluno().getMatricula(),
                        emprestimo.getAluno().getNome(),
                        emprestimo.getAluno().getTurma().getSerie(),
                        emprestimo.getLivro().getId(),
                        emprestimo.getLivro().getNome(),
                        emprestimo.getDataEmprestimo(),
                        emprestimo.getDataDevolucaoPrevista(),
                        emprestimo.getDiasAtrasados()));

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
        Aluno aluno = alunoRepository.findByMatricula(requestEmprestimo.matriculaAluno())
                .orElseThrow(AlunoNaoEncontradoException::new);
        Livro livro = livroRepository.findById(requestEmprestimo.fk_livro())
                .orElseThrow(LivroNaoEncontradoException::new);

        if (livro.getQuantidadeDisponivel() == 1) {
            livro.setStatus(StatusLivro.RESERVADO);
        }

        if (livro.getQuantidadeDisponivel() == 0) {
            throw new LivroIndisponivelException();
        }

        if (!aluno.podeFazerEmprestimo()) {
            throw new EmprestimoAtivoException();
        }

        List<Emprestimo> emprestimosAtivos = emprestimoRepository.findByStatusEmprestimo(StatusEmprestimo.ATIVO);

        emprestimosAtivos.forEach(e -> {
            if(e.getAluno().getMatricula().equals(requestEmprestimo.matriculaAluno())) {
                throw new AlunoComEmprestimoException();
            }
        });

        Emprestimo novoEmprestimo = new Emprestimo();

        novoEmprestimo.setAluno(aluno);
        novoEmprestimo.setLivro(livro);
        novoEmprestimo.setDataEmprestimo(requestEmprestimo.dataEmprestimo());
        novoEmprestimo.setDataDevolucaoPrevista(requestEmprestimo.dataEmprestimo().plusDays(qtdDias));
        novoEmprestimo.setStatusEmprestimo(setarStatusNaCriacaoEmprestimo(novoEmprestimo.getDataDevolucaoPrevista()));

        livro.setQuantidadeDisponivel(livro.getQuantidadeDisponivel() - 1);
        livroRepository.save(livro);

        return Optional.of(emprestimoRepository.save(novoEmprestimo));
    }

    @Override
    public Boolean finalizarEmprestimo(Long emprestimoId) {
        Emprestimo emprestimo = emprestimoRepository.findById(emprestimoId)
                .orElseThrow(EmprestimoNaoEncontradoException::new);

        emprestimo.setStatusEmprestimo(StatusEmprestimo.FINALIZADO);
        emprestimoRepository.save(emprestimo);

        alunoService.atualizarLivrosLidos(emprestimo.getAluno());

        if (emprestimo.getAluno().getQtdLivrosLidos() > 4) {
            alunoService.atualizarPontuacao(emprestimo.getAluno());
        }

        emprestimo.getLivro().setQuantidadeDisponivel(emprestimo.getLivro().getQuantidadeDisponivel() + 1);

        if (emprestimo.getLivro().getQuantidadeDisponivel() == 1) {
            emprestimo.getLivro().setStatus(StatusLivro.DISPONIVEL);
        }

        livroRepository.save(emprestimo.getLivro());

        return true;
    }

//    @Override
//    public List<Emprestimo> buscarEmprestimos(String valor) {
//
//        if(valor == null || valor.isEmpty()){
//            return emprestimoRepository.findAll();
//        }
//
//        List<Emprestimo> todosEmprestimos = emprestimoRepository.findAll();
//
//        boolean filterAtrasados = apenasAtrasados != null && apenasAtrasados;
//
//        if(filterAtrasados){
//
//            return emprestimoRepository.findAtrasadosByAlunoNomeOrLivroNomeContainingIgnoreCase(valor);
//        }
//
//       return emprestimoRepository.findByAlunoOrLivroAndStatus(valor);
//    }


    @Override
    public List<Emprestimo> buscarHistoricoLivro(Long idLivro) {
        Livro livro = livroRepository.findById(idLivro)
                .orElseThrow(() -> new RuntimeException("Livro não encontrado"));

        List<Emprestimo> listaFinalizados =  emprestimoRepository
                .findByStatusEmprestimo(StatusEmprestimo.FINALIZADO);

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
    public List<Emprestimo> buscarHistoricoAluno(Long matricula) {
        Aluno aluno = alunoRepository.findById(matricula)
                .orElseThrow(AlunoNaoEncontradoException::new);

        List<Emprestimo> emprestimosFinalizados = emprestimoRepository.findByStatusEmprestimo(StatusEmprestimo.FINALIZADO);

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

       return emprestimoRepository.findByStatusEmprestimo(StatusEmprestimo.ATRASADO);
    }

    public void resetarStatus() {
         List<Emprestimo> emprestimosFinalizados = emprestimoRepository.findByStatusEmprestimo(StatusEmprestimo.FINALIZADO);

         emprestimosFinalizados.forEach(emprestimo -> {
             emprestimo.setStatusEmprestimo(StatusEmprestimo.ARQUIVADO);
             emprestimoRepository.save(emprestimo);
         });
    }

    public StatusEmprestimo setarStatusNaCriacaoEmprestimo(LocalDate dataDevoluvaoPrevista) {
        LocalDate dataAual = LocalDate.now();

        if (dataAual.isAfter(dataDevoluvaoPrevista)) {
            return StatusEmprestimo.ATRASADO;
        } else  {
            return StatusEmprestimo.ATIVO;
        }

    }

}
