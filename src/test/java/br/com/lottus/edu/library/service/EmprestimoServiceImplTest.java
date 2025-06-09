package br.com.lottus.edu.library.service;

import br.com.lottus.edu.library.dto.RequestEmprestimo;
import br.com.lottus.edu.library.model.Aluno;
import br.com.lottus.edu.library.model.Emprestimo;
import br.com.lottus.edu.library.model.Livro;
import br.com.lottus.edu.library.model.StatusEmprestimo;
import br.com.lottus.edu.library.model.Turma;
import br.com.lottus.edu.library.model.Categoria;
import br.com.lottus.edu.library.repository.AlunoRepository;
import br.com.lottus.edu.library.repository.EmprestimoRepository;
import br.com.lottus.edu.library.repository.LivroRepository;
// Remova os imports de strategy se EmprestimoServiceImpl não os usa mais diretamente para buscarEmprestimos
// import br.com.lottus.edu.library.service.strategy.EmprestimoFiltroStrategy;
// import br.com.lottus.edu.library.service.strategy.EmprestimoFiltroStrategyFactory;

import br.com.lottus.edu.library.exception.AlunoComEmprestimoException;
import br.com.lottus.edu.library.exception.MultiClassNotFundException;
import br.com.lottus.edu.library.exception.EmprestimoNaoEncontradoException;
//import br.com.lottus.edu.library.exception.QuantidadeLivroInsuficienteException; // Adicione se for usar


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
// Remova MockedStatic se não for mais usado
// import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class EmprestimoServiceImplTest {

    @Mock
    private AlunoRepository alunoRepository;

    @Mock
    private LivroRepository livroRepository;

    @Mock
    private EmprestimoRepository emprestimoRepository;

    @Mock
    private AlunoService alunoService;

    // Remova se EmprestimoFiltroStrategy não for mais injetado ou usado
    // @Mock
    // private EmprestimoFiltroStrategy mockEstrategia;

    @InjectMocks
    private EmprestimoServiceImpl emprestimoService;

    private Aluno alunoComum;
    private Livro livroComum;
    private Emprestimo emprestimoAtivo;

    private Aluno alunoParaAtrasado;
    private Livro livroParaAtrasado;
    private Emprestimo emprestimoRealmenteAtrasado;

    private RequestEmprestimo requestEmprestimoPadrao;
    private final int QTD_DIAS_EMPRESTIMO_PADRAO = 15;
    private LocalDate dataEmprestimoFixa;

    @BeforeEach
    void setUp() {
        dataEmprestimoFixa = LocalDate.of(2023, 10, 26);

        Turma turma = new Turma();
        turma.setId(1L);
        turma.setSerie("Turma A");

        Categoria categoria = new Categoria();
        categoria.setId(1L);
        categoria.setNome("Ficção");

        alunoComum = new Aluno();
        alunoComum.setMatricula(1L);
        alunoComum.setNome("Aluno Teste Comum");
        alunoComum.setQtdLivrosLidos(0);
        alunoComum.setTurma(turma);

        livroComum = new Livro();
        livroComum.setId(1L);
        livroComum.setNome("Livro Teste Padrão");
        livroComum.setAutor("Autor Comum");
        livroComum.setQuantidade(1);
//        livroComum.setStatus(StatusLivro.DISPONIVEL);
        livroComum.setCategoria(categoria);

        requestEmprestimoPadrao = new RequestEmprestimo(alunoComum.getMatricula(), livroComum.getId(), 1L, dataEmprestimoFixa);

        emprestimoAtivo = new Emprestimo();
        emprestimoAtivo.setId(1L);
        emprestimoAtivo.setAluno(alunoComum);
        emprestimoAtivo.setLivro(livroComum);
        emprestimoAtivo.setDataEmprestimo(dataEmprestimoFixa);
        emprestimoAtivo.setDataDevolucaoPrevista(dataEmprestimoFixa.plusDays(QTD_DIAS_EMPRESTIMO_PADRAO));
        emprestimoAtivo.setStatusEmprestimo(StatusEmprestimo.ATIVO);

        alunoParaAtrasado = new Aluno();
        alunoParaAtrasado.setMatricula(2L);
        alunoParaAtrasado.setNome("Aluno Atrasildo Silva");
        alunoParaAtrasado.setQtdLivrosLidos(0);
        alunoParaAtrasado.setTurma(turma);

        livroParaAtrasado = new Livro();
        livroParaAtrasado.setId(2L);
        livroParaAtrasado.setNome("Manual do Atraso Vol I");
        livroParaAtrasado.setAutor("Autor Atrasado");
        livroParaAtrasado.setQuantidade(1);
//        livroParaAtrasado.setStatus(StatusLivro.DISPONIVEL); // Pode estar disponível, mas o empréstimo estar atrasado
        livroParaAtrasado.setCategoria(categoria);

        emprestimoRealmenteAtrasado = new Emprestimo();
        emprestimoRealmenteAtrasado.setId(2L);
        emprestimoRealmenteAtrasado.setAluno(alunoParaAtrasado);
        emprestimoRealmenteAtrasado.setLivro(livroParaAtrasado);
        emprestimoRealmenteAtrasado.setDataEmprestimo(dataEmprestimoFixa.minusDays(30));
        emprestimoRealmenteAtrasado.setDataDevolucaoPrevista(dataEmprestimoFixa.minusDays(30 - QTD_DIAS_EMPRESTIMO_PADRAO));
        emprestimoRealmenteAtrasado.setStatusEmprestimo(StatusEmprestimo.ATRASADO);

        emprestimoService.setApenasAtrasados(false);
    }

//    @Test
//    void fazerEmprestimo_ComSucesso() {
//        when(alunoRepository.findByMatricula(requestEmprestimoPadrao.matriculaAluno())).thenReturn(Optional.of(alunoComum));
//        when(livroRepository.findById(requestEmprestimoPadrao.fk_livro())).thenReturn(Optional.of(livroComum));
//        when(emprestimoRepository.findAllByStatusEmprestimo(StatusEmprestimo.ATIVO)).thenReturn(Collections.emptyList());
//        when(livroRepository.save(any(Livro.class))).thenReturn(livroComum);
//        when(emprestimoRepository.save(any(Emprestimo.class))).thenAnswer(invocation -> {
//            Emprestimo emprestimoSalvo = invocation.getArgument(0);
//            emprestimoSalvo.setId(1L);
//            return emprestimoSalvo;
//        });
//
//        Optional<Emprestimo> resultado = emprestimoService.fazerEmprestimo(requestEmprestimoPadrao);
//
//        assertTrue(resultado.isPresent());
//        assertEquals(alunoComum, resultado.get().getAluno());
//        assertEquals(livroComum, resultado.get().getLivro());
//        assertEquals(StatusEmprestimo.ATIVO, resultado.get().getStatusEmprestimo());
//        assertEquals(dataEmprestimoFixa.plusDays(emprestimoService.getQtdDias()), resultado.get().getDataDevolucaoPrevista());
//        assertEquals(StatusLivro.EMPRESTADO, livroComum.getStatus());
//        assertEquals(0, livroComum.getQuantidade());
//
//        verify(alunoRepository, times(1)).findByMatricula(requestEmprestimoPadrao.matriculaAluno());
//        verify(livroRepository, times(1)).findById(requestEmprestimoPadrao.fk_livro());
//        verify(emprestimoRepository, times(1)).findAllByStatusEmprestimo(StatusEmprestimo.ATIVO);
//        verify(livroRepository, times(1)).save(livroComum);
//        verify(emprestimoRepository, times(1)).save(any(Emprestimo.class));
//    }

    @Test
    void fazerEmprestimo_Falha_AlunoJaPossuiEmprestimoAtivo() {
        Emprestimo emprestimoExistente = new Emprestimo();
        emprestimoExistente.setAluno(alunoComum);
        emprestimoExistente.setStatusEmprestimo(StatusEmprestimo.ATIVO);

        when(alunoRepository.findByMatricula(requestEmprestimoPadrao.matriculaAluno())).thenReturn(Optional.of(alunoComum));
        when(livroRepository.findById(requestEmprestimoPadrao.fk_livro())).thenReturn(Optional.of(livroComum));
        when(emprestimoRepository.findByStatusEmprestimo(StatusEmprestimo.ATIVO))
                .thenReturn(Collections.singletonList(emprestimoExistente));

        assertThrows(AlunoComEmprestimoException.class, () -> {
            emprestimoService.fazerEmprestimo(requestEmprestimoPadrao);
        });

        verify(alunoRepository, times(1)).findByMatricula(requestEmprestimoPadrao.matriculaAluno());
        verify(livroRepository, times(1)).findById(requestEmprestimoPadrao.fk_livro());
        verify(emprestimoRepository, times(1)).findByStatusEmprestimo(StatusEmprestimo.ATIVO);
        verify(emprestimoRepository, never()).save(any(Emprestimo.class));
    }

    @Test
    void fazerEmprestimo_Falha_AlunoNaoEncontrado() {
        when(alunoRepository.findByMatricula(requestEmprestimoPadrao.matriculaAluno())).thenReturn(Optional.empty());
        when(livroRepository.findById(requestEmprestimoPadrao.fk_livro())).thenReturn(Optional.of(livroComum));


        assertThrows(MultiClassNotFundException.class, () -> {
            emprestimoService.fazerEmprestimo(requestEmprestimoPadrao);
        });

        verify(alunoRepository, times(1)).findByMatricula(requestEmprestimoPadrao.matriculaAluno());
        verify(livroRepository, times(1)).findById(requestEmprestimoPadrao.fk_livro());
        verify(emprestimoRepository, times(1)).findByStatusEmprestimo(StatusEmprestimo.ATIVO);
        verify(emprestimoRepository, never()).save(any(Emprestimo.class));
    }

    @Test
    void fazerEmprestimo_Falha_LivroNaoEncontrado() {
        when(alunoRepository.findByMatricula(requestEmprestimoPadrao.matriculaAluno())).thenReturn(Optional.of(alunoComum));
        when(livroRepository.findById(requestEmprestimoPadrao.fk_livro())).thenReturn(Optional.empty());


        assertThrows(MultiClassNotFundException.class, () -> {
            emprestimoService.fazerEmprestimo(requestEmprestimoPadrao);
        });

        verify(alunoRepository, times(1)).findByMatricula(requestEmprestimoPadrao.matriculaAluno());
        verify(livroRepository, times(1)).findById(requestEmprestimoPadrao.fk_livro());
        verify(emprestimoRepository, times(1)).findByStatusEmprestimo(StatusEmprestimo.ATIVO);
        verify(emprestimoRepository, never()).save(any(Emprestimo.class));
    }

//    @Test
//    void fazerEmprestimo_Falha_LivroNaoDisponivel() {
//        livroComum.setStatus(StatusLivro.EMPRESTADO);
//        when(alunoRepository.findByMatricula(requestEmprestimoPadrao.matriculaAluno())).thenReturn(Optional.of(alunoComum));
//        when(livroRepository.findById(requestEmprestimoPadrao.fk_livro())).thenReturn(Optional.of(livroComum));
//        when(emprestimoRepository.findAllByStatusEmprestimo(StatusEmprestimo.ATIVO)).thenReturn(Collections.emptyList());
//
//        assertThrows(LivroIndisponivelException.class, () -> {
//            emprestimoService.fazerEmprestimo(requestEmprestimoPadrao);
//        });
//        verify(emprestimoRepository, never()).save(any(Emprestimo.class));
//    }

//    @Test
//    void fazerEmprestimo_Falha_LivroComQuantidadeZero() {
//        livroComum.setQuantidade(0);
//        when(alunoRepository.findByMatricula(requestEmprestimoPadrao.matriculaAluno())).thenReturn(Optional.of(alunoComum));
//        when(livroRepository.findById(requestEmprestimoPadrao.fk_livro())).thenReturn(Optional.of(livroComum));
//        when(emprestimoRepository.findAllByStatusEmprestimo(StatusEmprestimo.ATIVO)).thenReturn(Collections.emptyList());
//
//        assertThrows(QuantidadeLivroInsuficienteException.class, () -> {
//            emprestimoService.fazerEmprestimo(requestEmprestimoPadrao);
//        });
//        verify(emprestimoRepository, never()).save(any(Emprestimo.class));
//    }


//    @Test
//    void finalizarEmprestimo_ComSucesso_AlunoLidosMenorOuIgual4() {
//        alunoComum.setQtdLivrosLidos(3);
//        emprestimoAtivo.setAluno(alunoComum);
//        livroComum.setStatus(StatusLivro.EMPRESTADO);
//        livroComum.setQuantidade(0);
//
//        when(emprestimoRepository.findById(emprestimoAtivo.getId())).thenReturn(Optional.of(emprestimoAtivo));
//        when(emprestimoRepository.save(any(Emprestimo.class))).thenReturn(emprestimoAtivo);
//        when(livroRepository.save(any(Livro.class))).thenReturn(livroComum);
//        doNothing().when(alunoService).atualizarLivrosLidos(alunoComum);
//
//        Boolean resultado = emprestimoService.finalizarEmprestimo(emprestimoAtivo.getId());
//
//        assertTrue(resultado);
//        assertEquals(StatusEmprestimo.FINALIZADO, emprestimoAtivo.getStatusEmprestimo());
//        assertEquals(StatusLivro.DISPONIVEL, livroComum.getStatus());
//        assertEquals(1, livroComum.getQuantidade());
//
//        verify(emprestimoRepository, times(1)).findById(emprestimoAtivo.getId());
//        verify(alunoService, times(1)).atualizarLivrosLidos(alunoComum);
//        verify(alunoService, never()).atualizarPontuacao(alunoComum);
//        verify(livroRepository, times(1)).save(livroComum);
//        verify(emprestimoRepository, times(1)).save(emprestimoAtivo);
//    }

//    @Test
//    void finalizarEmprestimo_ComSucesso_AlunoLidosMaiorQue4() {
//        alunoComum.setQtdLivrosLidos(5);
//        emprestimoAtivo.setAluno(alunoComum);
//        livroComum.setStatus(StatusLivro.EMPRESTADO);
//        livroComum.setQuantidade(0);
//
//        when(emprestimoRepository.findById(emprestimoAtivo.getId())).thenReturn(Optional.of(emprestimoAtivo));
//        when(emprestimoRepository.save(any(Emprestimo.class))).thenReturn(emprestimoAtivo);
//        when(livroRepository.save(any(Livro.class))).thenReturn(livroComum);
//        doNothing().when(alunoService).atualizarLivrosLidos(alunoComum);
//        doNothing().when(alunoService).atualizarPontuacao(alunoComum);
//
//        Boolean resultado = emprestimoService.finalizarEmprestimo(emprestimoAtivo.getId());
//
//        assertTrue(resultado);
//        assertEquals(StatusEmprestimo.FINALIZADO, emprestimoAtivo.getStatusEmprestimo());
//        assertEquals(StatusLivro.DISPONIVEL, livroComum.getStatus());
//        assertEquals(1, livroComum.getQuantidade());
//
//        verify(alunoService, times(1)).atualizarLivrosLidos(alunoComum);
//        verify(alunoService, times(1)).atualizarPontuacao(alunoComum);
//    }

    @Test
    void finalizarEmprestimo_Falha_EmprestimoNaoEncontrado() {
        Long idInexistente = 99L;
        when(emprestimoRepository.findById(idInexistente)).thenReturn(Optional.empty());

        assertThrows(EmprestimoNaoEncontradoException.class, () -> {
            emprestimoService.finalizarEmprestimo(idInexistente);
        });
        verify(emprestimoRepository, never()).save(any(Emprestimo.class));
    }

    @Test
    void finalizarEmprestimo_Falha_EmprestimoJaFinalizado() {
        emprestimoAtivo.setStatusEmprestimo(StatusEmprestimo.FINALIZADO);
        when(emprestimoRepository.findById(emprestimoAtivo.getId())).thenReturn(Optional.of(emprestimoAtivo));

        assertThrows(IllegalStateException.class, () -> {
            emprestimoService.finalizarEmprestimo(emprestimoAtivo.getId());
        });
        verify(emprestimoRepository, never()).save(any(Emprestimo.class));
    }


    @Test
    @DisplayName("filtrarEmprestimosAtrasados: Quando apenasAtrasados era false, deve mudar para true e retornar atrasados")
    void filtrarEmprestimosAtrasados_QuandoApenasAtrasadosEraFalse_DeveMudarParaTrueERetornarAtrasados() {
        emprestimoService.setApenasAtrasados(false);
        List<Emprestimo> listaAtrasados = List.of(emprestimoRealmenteAtrasado);
        when(emprestimoRepository.findByStatusEmprestimo(StatusEmprestimo.ATRASADO)).thenReturn(listaAtrasados);

        List<Emprestimo> resultado = emprestimoService.filtrarEmprestimosAtrasados();

        assertTrue(emprestimoService.getApenasAtrasados());
        assertEquals(listaAtrasados, resultado);
        verify(emprestimoRepository).findByStatusEmprestimo(StatusEmprestimo.ATRASADO);
    }

    @Test
    @DisplayName("filtrarEmprestimosAtrasados: Quando apenasAtrasados era true, deve mudar para false e retornar todos")
    void filtrarEmprestimosAtrasados_QuandoApenasAtrasadosEraTrue_DeveMudarParaFalseERetornarTodos() {
        emprestimoService.setApenasAtrasados(true);
        List<Emprestimo> listaTodos = List.of(emprestimoAtivo, emprestimoRealmenteAtrasado);
        when(emprestimoRepository.findAll()).thenReturn(listaTodos);

        List<Emprestimo> resultado = emprestimoService.filtrarEmprestimosAtrasados();

        assertFalse(emprestimoService.getApenasAtrasados());
        assertEquals(listaTodos, resultado);
        verify(emprestimoRepository).findAll();
    }


    @Test
    @DisplayName("buscarEmprestimos: Com valor nulo, deve retornar todos os empréstimos")
    void buscarEmprestimos_ComValorNulo_DeveRetornarTodosOsEmprestimos() {
        List<Emprestimo> todosEmprestimos = List.of(emprestimoAtivo, emprestimoRealmenteAtrasado);
        when(emprestimoRepository.findAll()).thenReturn(todosEmprestimos);

        List<Emprestimo> resultado = emprestimoService.buscarEmprestimos(null);
        assertEquals(todosEmprestimos, resultado);
        verify(emprestimoRepository).findAll();
    }

    @Test
    @DisplayName("buscarEmprestimos: Com valor vazio, deve retornar todos os empréstimos")
    void buscarEmprestimos_ComValorVazio_DeveRetornarTodosOsEmprestimos() {
        List<Emprestimo> todosEmprestimos = List.of(emprestimoAtivo, emprestimoRealmenteAtrasado);
        when(emprestimoRepository.findAll()).thenReturn(todosEmprestimos);
        List<Emprestimo> resultado = emprestimoService.buscarEmprestimos("");
        assertEquals(todosEmprestimos, resultado);
        verify(emprestimoRepository).findAll();
    }


    @Test
    @DisplayName("buscarEmprestimos: Com valor e apenasAtrasados=false, deve chamar findByAlunoNomeOuLivroNome")
    void buscarEmprestimos_ComValor_E_apenasAtrasadosFalse_DeveChamarFindByAlunoNomeOuLivroNome() {
        emprestimoService.setApenasAtrasados(false);
        String termo = "Aluno Comum";
        List<Emprestimo> listaEsperada = List.of(emprestimoAtivo);
        when(emprestimoRepository.findByAlunoOrLivroAndStatus(termo)).thenReturn(listaEsperada);

        List<Emprestimo> resultado = emprestimoService.buscarEmprestimos(termo);

        assertEquals(listaEsperada, resultado);
        assertFalse(emprestimoService.getApenasAtrasados());
        verify(emprestimoRepository).findByAlunoOrLivroAndStatus(termo);
    }

    @Test
    @DisplayName("buscarEmprestimos: Com valor e apenasAtrasados=true, deve chamar findAtrasadosByAlunoNomeOuLivroNome")
    void buscarEmprestimos_ComValor_E_apenasAtrasadosTrue_DeveChamarFindAtrasadosByAlunoNomeOuLivroNome() {
        emprestimoService.setApenasAtrasados(true);
        String termo = "Aluno i";
        List<Emprestimo> listaEsperada = List.of(emprestimoRealmenteAtrasado);
        when(emprestimoRepository.findAtrasadosByAlunoNomeOrLivroNomeContainingIgnoreCase(termo)).thenReturn(listaEsperada);

        List<Emprestimo> resultado = emprestimoService.buscarEmprestimos(termo);

        assertEquals(listaEsperada, resultado);
        assertTrue(emprestimoService.getApenasAtrasados());
        verify(emprestimoRepository).findAtrasadosByAlunoNomeOrLivroNomeContainingIgnoreCase(termo);
    }

    @Test
    @DisplayName("buscarEmprestimos: Com valor não encontrado e apenasAtrasados=false, deve retornar lista vazia")
    void buscarEmprestimos_ComValorNaoEncontrado_E_apenasAtrasadosFalse_DeveRetornarListaVazia() {
        emprestimoService.setApenasAtrasados(false);
        String termo = "Inexistente";
        when(emprestimoRepository.findByAlunoOrLivroAndStatus(termo)).thenReturn(Collections.emptyList());

        List<Emprestimo> resultado = emprestimoService.buscarEmprestimos(termo);

        assertTrue(resultado.isEmpty());
        verify(emprestimoRepository).findByAlunoOrLivroAndStatus(termo);
    }

    @Test
    @DisplayName("buscarEmprestimos: Com valor não encontrado e apenasAtrasados=true, deve retornar lista vazia")
    void buscarEmprestimos_ComValorNaoEncontrado_E_apenasAtrasadosTrue_DeveRetornarListaVazia() {
        emprestimoService.setApenasAtrasados(true);
        String termo = "Inexistente";
        when(emprestimoRepository.findAtrasadosByAlunoNomeOrLivroNomeContainingIgnoreCase(termo)).thenReturn(Collections.emptyList());

        List<Emprestimo> resultado = emprestimoService.buscarEmprestimos(termo);

        assertTrue(resultado.isEmpty());
        verify(emprestimoRepository).findAtrasadosByAlunoNomeOrLivroNomeContainingIgnoreCase(termo);
    }
}