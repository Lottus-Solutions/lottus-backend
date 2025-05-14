package br.com.lottus.edu.library.service;

import br.com.lottus.edu.library.dto.RequestEmprestimo;
import br.com.lottus.edu.library.exception.LivroNaoEncontradoException;
import br.com.lottus.edu.library.model.Aluno;
import br.com.lottus.edu.library.model.Emprestimo;
import br.com.lottus.edu.library.model.Livro;
import br.com.lottus.edu.library.model.StatusEmprestimo;
import br.com.lottus.edu.library.model.StatusLivro;
import br.com.lottus.edu.library.model.Turma;
import br.com.lottus.edu.library.model.Categoria;
import br.com.lottus.edu.library.repository.AlunoRepository;
import br.com.lottus.edu.library.repository.EmprestimoRepository;
import br.com.lottus.edu.library.repository.LivroRepository;
import br.com.lottus.edu.library.service.strategy.EmprestimoFiltroStrategy;
import br.com.lottus.edu.library.service.strategy.EmprestimoFiltroStrategyFactory;

import br.com.lottus.edu.library.exception.AlunoComEmprestimoException;
import br.com.lottus.edu.library.exception.MultiClassNotFundException;
import br.com.lottus.edu.library.exception.EmprestimoNaoEncontradoException;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
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


    @Mock
    private EmprestimoFiltroStrategy mockEstrategia;

    @InjectMocks
    private EmprestimoServiceImpl emprestimoService;

    private Aluno aluno;
    private Livro livro;
    private Emprestimo emprestimo;
    private RequestEmprestimo requestEmprestimo;
    private final int QTD_DIAS_EMPRESTIMO_PADRAO = 15;
    private LocalDate dataEmprestimoFixa;

    @BeforeEach
    void setUp() {
        dataEmprestimoFixa = LocalDate.of(2023,10,26);

        Turma turma = new Turma();
        turma.setId(1L);
        turma.setSerie("Turma A");

        aluno = new Aluno();
        aluno.setMatricula(1L);
        aluno.setNome("Aluno Teste");
        aluno.setQtdLivrosLidos(0);
        aluno.setTurma(turma);

        Categoria categoria = new Categoria();
        categoria.setId(1L);
        categoria.setNome("Ficção");

        livro = new Livro();
        livro.setId(1L);
        livro.setNome("Livro Teste");
        livro.setAutor("Autor Teste");
        livro.setQuantidade(1);
        livro.setStatus(StatusLivro.DISPONIVEL);
        livro.setCategoria(categoria);

        requestEmprestimo = new RequestEmprestimo(aluno.getMatricula(), livro.getId(), 1L, dataEmprestimoFixa);

        emprestimo = new Emprestimo();
        emprestimo.setId(1L);
        emprestimo.setAluno(aluno);
        emprestimo.setLivro(livro);
        emprestimo.setDataEmprestimo(dataEmprestimoFixa);
        emprestimo.setDataDevolucaoPrevista(dataEmprestimoFixa.plusDays(QTD_DIAS_EMPRESTIMO_PADRAO));
        emprestimo.setStatusEmprestimo(StatusEmprestimo.ATIVO);
    }

    @Test
    void fazerEmprestimo_ComSucesso() {
        when(alunoRepository.findByMatricula(requestEmprestimo.matriculaAluno())).thenReturn(Optional.of(aluno));
        when(livroRepository.findById(requestEmprestimo.fk_livro())).thenReturn(Optional.of(livro));
        when(emprestimoRepository.findAllByStatusEmprestimo(StatusEmprestimo.ATIVO)).thenReturn(Collections.emptyList());
        when(emprestimoRepository.save(any(Emprestimo.class))).thenAnswer(invocation -> {
            Emprestimo emprestimoSalvo = invocation.getArgument(0);
            emprestimoSalvo.setId(1L);
            return emprestimoSalvo;
        });

        Optional<Emprestimo> resultado = emprestimoService.fazerEmprestimo(requestEmprestimo);

        assertTrue(resultado.isPresent());
        assertEquals(aluno, resultado.get().getAluno());
        assertEquals(livro, resultado.get().getLivro());
        assertEquals(StatusEmprestimo.ATIVO, resultado.get().getStatusEmprestimo());
        assertEquals(dataEmprestimoFixa.plusDays(emprestimoService.getQtdDias()), resultado.get().getDataDevolucaoPrevista());

        verify(alunoRepository, times(1)).findByMatricula(requestEmprestimo.matriculaAluno());
        verify(livroRepository, times(1)).findById(requestEmprestimo.fk_livro());
        verify(emprestimoRepository, times(1)).findAllByStatusEmprestimo(StatusEmprestimo.ATIVO);
        verify(emprestimoRepository, times(1)).save(any(Emprestimo.class));
    }

    @Test
    void fazerEmprestimo_Falha_AlunoJaPossuiEmprestimoAtivo() {
        Emprestimo emprestimoExistente = new Emprestimo();
        emprestimoExistente.setAluno(aluno);
        emprestimoExistente.setStatusEmprestimo(StatusEmprestimo.ATIVO);

        when(alunoRepository.findByMatricula(requestEmprestimo.matriculaAluno())).thenReturn(Optional.of(aluno));
        when(livroRepository.findById(requestEmprestimo.fk_livro())).thenReturn(Optional.of(livro));
        when(emprestimoRepository.findAllByStatusEmprestimo(StatusEmprestimo.ATIVO))
                .thenReturn(Collections.singletonList(emprestimoExistente));

        assertThrows(AlunoComEmprestimoException.class, () -> {
            emprestimoService.fazerEmprestimo(requestEmprestimo);
        });

        verify(alunoRepository, times(1)).findByMatricula(requestEmprestimo.matriculaAluno());
        verify(livroRepository, times(1)).findById(requestEmprestimo.fk_livro());
        verify(emprestimoRepository, times(1)).findAllByStatusEmprestimo(StatusEmprestimo.ATIVO);
        verify(emprestimoRepository, never()).save(any(Emprestimo.class));
    }

    @Test
    void fazerEmprestimo_Falha_AlunoNaoEncontrado() {
        when(alunoRepository.findByMatricula(requestEmprestimo.matriculaAluno())).thenReturn(Optional.empty());
        when(livroRepository.findById(requestEmprestimo.fk_livro())).thenReturn(Optional.of(livro));

        assertThrows(MultiClassNotFundException.class, () -> {
            emprestimoService.fazerEmprestimo(requestEmprestimo);
        });

        verify(alunoRepository, times(1)).findByMatricula(requestEmprestimo.matriculaAluno());
        verify(livroRepository, times(1)).findById(requestEmprestimo.fk_livro());
        verify(emprestimoRepository, times(1)).findAllByStatusEmprestimo(StatusEmprestimo.ATIVO);
        verify(emprestimoRepository, never()).save(any(Emprestimo.class));
    }

    @Test
    void fazerEmprestimo_Falha_LivroNaoEncontrado() {
        when(alunoRepository.findByMatricula(requestEmprestimo.matriculaAluno())).thenReturn(Optional.of(aluno));
        when(livroRepository.findById(requestEmprestimo.fk_livro())).thenReturn(Optional.empty());

        assertThrows(MultiClassNotFundException.class, () -> {
            emprestimoService.fazerEmprestimo(requestEmprestimo);
        });

        verify(alunoRepository, times(1)).findByMatricula(requestEmprestimo.matriculaAluno());
        verify(livroRepository, times(1)).findById(requestEmprestimo.fk_livro());
        verify(emprestimoRepository, times(1)).findAllByStatusEmprestimo(StatusEmprestimo.ATIVO);
        verify(emprestimoRepository, never()).save(any(Emprestimo.class));
    }

    @Test
    void finalizarEmprestimo_ComSucesso_AlunoLidosMenorOuIgual4() {
        aluno.setQtdLivrosLidos(3);
        emprestimo.setAluno(aluno);
        when(emprestimoRepository.findById(emprestimo.getId())).thenReturn(Optional.of(emprestimo));
        when(emprestimoRepository.save(any(Emprestimo.class))).thenReturn(emprestimo);
        doNothing().when(alunoService).atualizarLivrosLidos(aluno);

        Boolean resultado = emprestimoService.finalizarEmprestimo(emprestimo.getId());

        assertTrue(resultado);
        assertEquals(StatusEmprestimo.FINALIZADO, emprestimo.getStatusEmprestimo());

        verify(emprestimoRepository, times(1)).findById(emprestimo.getId());
        verify(alunoService, times(1)).atualizarLivrosLidos(aluno);
        verify(alunoService, never()).atualizarPontuacao(aluno);
        verify(emprestimoRepository, times(2)).save(emprestimo);
    }

    @Test
    void finalizarEmprestimo_ComSucesso_AlunoLidosMaiorQue4() {
        aluno.setQtdLivrosLidos(5);
        emprestimo.setAluno(aluno);
        when(emprestimoRepository.findById(emprestimo.getId())).thenReturn(Optional.of(emprestimo));
        when(emprestimoRepository.save(any(Emprestimo.class))).thenReturn(emprestimo);
        doNothing().when(alunoService).atualizarLivrosLidos(aluno);
        doNothing().when(alunoService).atualizarPontuacao(aluno);

        Boolean resultado = emprestimoService.finalizarEmprestimo(emprestimo.getId());

        assertTrue(resultado);
        assertEquals(StatusEmprestimo.FINALIZADO, emprestimo.getStatusEmprestimo());

        verify(emprestimoRepository, times(1)).findById(emprestimo.getId());
        verify(alunoService, times(1)).atualizarLivrosLidos(aluno); // Corrigido
        verify(alunoService, times(1)).atualizarPontuacao(aluno);
        verify(emprestimoRepository, times(2)).save(emprestimo);
    }

    @Test
    void finalizarEmprestimo_Falha_EmprestimoNaoEncontrado() {
        Long idInexistente = 99L;
        when(emprestimoRepository.findById(idInexistente)).thenReturn(Optional.empty());

        assertThrows(EmprestimoNaoEncontradoException.class, () -> {
            emprestimoService.finalizarEmprestimo(idInexistente);
        });

        verify(emprestimoRepository, times(1)).findById(idInexistente);
        verify(alunoService, never()).atualizarLivrosLidos(any(Aluno.class));
        verify(alunoService, never()).atualizarPontuacao(any(Aluno.class));
        verify(emprestimoRepository, never()).save(any(Emprestimo.class));
    }

    @Test
    void buscarEmprestimos_SemFiltros() {
        List<Emprestimo> todosEmprestimos = List.of(emprestimo, new Emprestimo());
        when(emprestimoRepository.findAll()).thenReturn(todosEmprestimos);

        try (MockedStatic<EmprestimoFiltroStrategyFactory> mockedFactory = mockStatic(EmprestimoFiltroStrategyFactory.class)) {
            // O método buscarEmprestimos(null, null, null) internamente chamará
            // criarEstrategia(null, null, false) porque 'apenasAtrasados' será false.
            mockedFactory.when(() -> EmprestimoFiltroStrategyFactory.criarEstrategia(null, null, false))
                    .thenReturn(mockEstrategia);
            when(mockEstrategia.filtrar(todosEmprestimos)).thenReturn(todosEmprestimos);

            List<Emprestimo> resultado = emprestimoService.buscarEmprestimos(null, null, null);

            assertNotNull(resultado);
            assertEquals(2, resultado.size());

            verify(livroRepository, never()).findById(anyLong());
            verify(alunoRepository, never()).findByMatricula(anyLong());
            verify(emprestimoRepository, times(1)).findAll();
            mockedFactory.verify(() -> EmprestimoFiltroStrategyFactory.criarEstrategia(null, null, false));
            verify(mockEstrategia, times(1)).filtrar(todosEmprestimos);
        }
    }

    @Test
    void buscarEmprestimos_ComLivroId_LivroEncontrado() {
        Long livroId = livro.getId();
        List<Emprestimo> emprestimosFiltrados = List.of(emprestimo);
        List<Emprestimo> todosEmprestimos = List.of(emprestimo, new Emprestimo());

        when(livroRepository.findById(livroId)).thenReturn(Optional.of(livro));
        when(emprestimoRepository.findAll()).thenReturn(todosEmprestimos);

        try (MockedStatic<EmprestimoFiltroStrategyFactory> mockedFactory = mockStatic(EmprestimoFiltroStrategyFactory.class)) {
            // buscarEmprestimos(livroId, null, false) -> criarEstrategia(livro, null, false)
            mockedFactory.when(() -> EmprestimoFiltroStrategyFactory.criarEstrategia(eq(livro), isNull(), eq(false)))
                    .thenReturn(mockEstrategia);
            when(mockEstrategia.filtrar(todosEmprestimos)).thenReturn(emprestimosFiltrados);

            List<Emprestimo> resultado = emprestimoService.buscarEmprestimos(livroId, null, false);

            assertNotNull(resultado);
            assertEquals(1, resultado.size());
            assertEquals(emprestimo, resultado.getFirst());

            verify(livroRepository, times(1)).findById(livroId);
            verify(alunoRepository, never()).findByMatricula(anyLong());
            verify(emprestimoRepository, times(1)).findAll();
            mockedFactory.verify(() -> EmprestimoFiltroStrategyFactory.criarEstrategia(eq(livro), isNull(), eq(false)));
            verify(mockEstrategia, times(1)).filtrar(todosEmprestimos);
        }
    }

    @Test
    void buscarEmprestimos_ComLivroId_LivroNaoEncontrado() {
        Long livroId = 99L;
        when(livroRepository.findById(livroId)).thenReturn(Optional.empty());

        List<Emprestimo> resultado = emprestimoService.buscarEmprestimos(livroId, null, null);

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());

        verify(livroRepository, times(1)).findById(livroId);
        verify(alunoRepository, never()).findByMatricula(anyLong());
        verify(emprestimoRepository, never()).findAll(); // Não deve chamar findAll se o livro não for encontrado

    }

    @Test
    void buscarEmprestimos_ComMatricula_AlunoEncontrado() {
        Long matricula = aluno.getMatricula();
        List<Emprestimo> emprestimosFiltrados = List.of(emprestimo);
        List<Emprestimo> todosEmprestimos = List.of(emprestimo, new Emprestimo());

        when(alunoRepository.findByMatricula(matricula)).thenReturn(Optional.of(aluno));
        when(emprestimoRepository.findAll()).thenReturn(todosEmprestimos);

        try (MockedStatic<EmprestimoFiltroStrategyFactory> mockedFactory = mockStatic(EmprestimoFiltroStrategyFactory.class)) {

            mockedFactory.when(() -> EmprestimoFiltroStrategyFactory.criarEstrategia(isNull(), eq(aluno), eq(true)))
                    .thenReturn(mockEstrategia);
            when(mockEstrategia.filtrar(todosEmprestimos)).thenReturn(emprestimosFiltrados);

            List<Emprestimo> resultado = emprestimoService.buscarEmprestimos(null, matricula, true);

            assertNotNull(resultado);
            assertEquals(1, resultado.size());
            assertEquals(emprestimo, resultado.getFirst());

            verify(livroRepository, never()).findById(anyLong());
            verify(alunoRepository, times(1)).findByMatricula(matricula);
            verify(emprestimoRepository, times(1)).findAll();
            mockedFactory.verify(() -> EmprestimoFiltroStrategyFactory.criarEstrategia(isNull(), eq(aluno), eq(true)));
            verify(mockEstrategia, times(1)).filtrar(todosEmprestimos);
        }
    }

    @Test
    void buscarEmprestimos_ComMatricula_AlunoNaoEncontrado() {
        Long matricula = 99L;
        when(alunoRepository.findByMatricula(matricula)).thenReturn(Optional.empty());

        // Semelhante ao livro não encontrado, a factory não deve ser chamada.
        List<Emprestimo> resultado = emprestimoService.buscarEmprestimos(null, matricula, null);

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());

        verify(livroRepository, never()).findById(anyLong());
        verify(alunoRepository, times(1)).findByMatricula(matricula);
        verify(emprestimoRepository, never()).findAll();
    }

    @Test
    void buscarEmprestimos_ComTodosParametros_EntidadesEncontradas() {
        Long livroId = livro.getId();
        Long matricula = aluno.getMatricula();
        Boolean apenasAtrasados = true; // No seu serviço: filterAtrasados = apenasAtrasados != null && apenasAtrasados; -> true

        List<Emprestimo> emprestimosFiltrados = List.of(emprestimo);
        List<Emprestimo> todosEmprestimos = List.of(emprestimo, new Emprestimo());

        when(livroRepository.findById(livroId)).thenReturn(Optional.of(livro));
        when(alunoRepository.findByMatricula(matricula)).thenReturn(Optional.of(aluno));
        when(emprestimoRepository.findAll()).thenReturn(todosEmprestimos);

        try (MockedStatic<EmprestimoFiltroStrategyFactory> mockedFactory = mockStatic(EmprestimoFiltroStrategyFactory.class)) {
            mockedFactory.when(() -> EmprestimoFiltroStrategyFactory.criarEstrategia(eq(livro), eq(aluno), eq(true)))
                    .thenReturn(mockEstrategia);
            when(mockEstrategia.filtrar(todosEmprestimos)).thenReturn(emprestimosFiltrados);

            List<Emprestimo> resultado = emprestimoService.buscarEmprestimos(livroId, matricula, apenasAtrasados);

            assertNotNull(resultado);
            assertEquals(1, resultado.size());
            assertEquals(emprestimo, resultado.getFirst());

            verify(livroRepository, times(1)).findById(livroId);
            verify(alunoRepository, times(1)).findByMatricula(matricula);
            verify(emprestimoRepository, times(1)).findAll();
            mockedFactory.verify(() -> EmprestimoFiltroStrategyFactory.criarEstrategia(eq(livro), eq(aluno), eq(true)));
            verify(mockEstrategia, times(1)).filtrar(todosEmprestimos);
        }
    }

    @Test
    void buscarEmprestimos_ApenasAtrasadosTrue() {
        Boolean apenasAtrasados = true;
        List<Emprestimo> todosEmprestimos = List.of(emprestimo);

        when(emprestimoRepository.findAll()).thenReturn(todosEmprestimos);

        try (MockedStatic<EmprestimoFiltroStrategyFactory> mockedFactory = mockStatic(EmprestimoFiltroStrategyFactory.class)) {
            // buscarEmprestimos(null, null, true) -> criarEstrategia(null, null, true)
            mockedFactory.when(() -> EmprestimoFiltroStrategyFactory.criarEstrategia(isNull(), isNull(), eq(true)))
                    .thenReturn(mockEstrategia);
            when(mockEstrategia.filtrar(todosEmprestimos)).thenReturn(todosEmprestimos);

            List<Emprestimo> resultado = emprestimoService.buscarEmprestimos(null, null, apenasAtrasados);

            assertNotNull(resultado);
            assertEquals(1, resultado.size());

            verify(emprestimoRepository, times(1)).findAll();
            mockedFactory.verify(() -> EmprestimoFiltroStrategyFactory.criarEstrategia(isNull(), isNull(), eq(true)));
            verify(mockEstrategia, times(1)).filtrar(todosEmprestimos);
        }
    }

    @Test
    void fazerEmprestimo_LivroNaoDisponivel(){
        livro.setStatus(StatusLivro.RESERVADO);

        when(alunoRepository.findByMatricula(requestEmprestimo.matriculaAluno())).thenReturn(Optional.of(aluno));
        when(livroRepository.findById(requestEmprestimo.fk_livro()))
                .thenReturn(Optional.of(livro));

        when(emprestimoRepository.findAllByStatusEmprestimo(StatusEmprestimo.ATIVO))
                .thenReturn(Collections.emptyList());

        assertThrows(LivroNaoEncontradoException.class, () ->{
            emprestimoService.fazerEmprestimo(requestEmprestimo);
        });

        verify(alunoRepository, times(1)).findByMatricula(requestEmprestimo.matriculaAluno());
        verify(livroRepository, times(1)).findById(requestEmprestimo.fk_livro());
        verify(emprestimoRepository, times(1)).findAllByStatusEmprestimo(StatusEmprestimo.ATIVO);
        verify(emprestimoRepository, never()).save(any(Emprestimo.class));
        verify(livroRepository, never()).save(any(Livro.class));
    }


}