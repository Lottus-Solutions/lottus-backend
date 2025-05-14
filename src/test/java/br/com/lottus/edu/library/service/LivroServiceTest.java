package br.com.lottus.edu.library.service;

import br.com.lottus.edu.library.dto.LivroRequestDTO;
import br.com.lottus.edu.library.dto.LivroResponseDTO;
import br.com.lottus.edu.library.exception.CategoriaNaoEncontradaException;
import br.com.lottus.edu.library.exception.LivroNaoEncontradoException;
import br.com.lottus.edu.library.exception.NenhumLivroEncontradoException;
import br.com.lottus.edu.library.model.Categoria;
import br.com.lottus.edu.library.model.Livro;
import br.com.lottus.edu.library.model.StatusLivro;
import br.com.lottus.edu.library.repository.CategoriaRepository;
import br.com.lottus.edu.library.repository.LivroRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LivroServiceTest {

    @Mock
    private LivroRepository livroRepository;

    @Mock
    private CategoriaRepository categoriaRepository;

    @InjectMocks
    private LivroService livroService;

    private Categoria categoria;
    private Livro livro;
    private LivroRequestDTO livroRequestDTO;
    private LivroResponseDTO livroResponseDTO;

    @BeforeEach
    void setUp() {
        categoria = new Categoria(1L, "Ficção Científica", "Livros sobre o futuro e tecnologia");

        livro = new Livro("Duna", "Frank Herbert", categoria, StatusLivro.DISPONIVEL, 5);
        livro.setId(1L);

        livroRequestDTO = new LivroRequestDTO(
                "Duna",
                "Frank Herbert",
                5,
                StatusLivro.DISPONIVEL,
                categoria.getId()
        );

        livroResponseDTO = new LivroResponseDTO(
                livro.getId(),
                livro.getNome(),
                livro.getAutor(),
                livro.getQuantidade(),
                livro.getStatus(),
                categoria.getNome()
        );
    }

    // --- Testes para buscarTodos ---
    @Test
    void buscarTodos_QuandoExistemLivros_DeveRetornarListaDeLivrosDTO() {
        when(livroRepository.findAll()).thenReturn(List.of(livro));

        List<LivroResponseDTO> resultado = livroService.buscarTodos();

        assertNotNull(resultado);
        assertFalse(resultado.isEmpty());
        assertEquals(1, resultado.size());
        assertEquals(livroResponseDTO.nome(), resultado.getFirst().nome());
        verify(livroRepository, times(1)).findAll();
    }

    @Test
    void buscarTodos_QuandoNaoExistemLivros_DeveRetornarListaVazia() {
        when(livroRepository.findAll()).thenReturn(Collections.emptyList());

        List<LivroResponseDTO> resultado = livroService.buscarTodos();

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
        verify(livroRepository, times(1)).findAll();
    }

    // --- Testes para cadastrarLivro ---
    @Test
    void cadastrarLivro_ComDadosValidos_DeveRetornarLivroDTO() {
        when(categoriaRepository.findById(livroRequestDTO.categoriaId())).thenReturn(Optional.of(categoria));
        when(livroRepository.save(any(Livro.class))).thenAnswer(invocation -> {
            Livro livroSalvo = invocation.getArgument(0);
            livroSalvo.setId(1L); // Simula a geração do ID pelo BD
            return livroSalvo;
        });

        LivroResponseDTO resultado = livroService.cadastrarLivro(livroRequestDTO);

        assertNotNull(resultado);
        assertEquals(livroRequestDTO.nome(), resultado.nome());
        assertEquals(livroRequestDTO.autor(), resultado.autor());
        assertEquals(categoria.getNome(), resultado.categoria());
        assertEquals(1L, resultado.id());

        verify(categoriaRepository, times(1)).findById(livroRequestDTO.categoriaId());
        verify(livroRepository, times(1)).save(any(Livro.class));
    }

    @Test
    void cadastrarLivro_QuandoCategoriaNaoEncontrada_DeveLancarCategoriaNaoEncontradaException() {
        Long idCategoriaInexistente = 99L;
        LivroRequestDTO requestComCategoriaInvalida = new LivroRequestDTO(
                "Livro Teste", "Autor", 1, StatusLivro.DISPONIVEL, idCategoriaInexistente
        );
        when(categoriaRepository.findById(idCategoriaInexistente)).thenReturn(Optional.empty());

        assertThrows(CategoriaNaoEncontradaException.class, () -> {
            livroService.cadastrarLivro(requestComCategoriaInvalida);
        });

        verify(categoriaRepository, times(1)).findById(idCategoriaInexistente);
        verify(livroRepository, never()).save(any(Livro.class));
    }

    // --- Testes para atualizarLivro ---
    @Test
    void atualizarLivro_ComDadosValidos_DeveRetornarLivroDTOAtualizado() {
        Long livroIdExistente = 1L;
        LivroRequestDTO requestAtualizacao = new LivroRequestDTO(
                "Duna Atualizado", "Frank Herbert Jr.", 10, StatusLivro.RESERVADO, categoria.getId()
        );

        when(livroRepository.findById(livroIdExistente)).thenReturn(Optional.of(livro));
        when(categoriaRepository.findById(requestAtualizacao.categoriaId())).thenReturn(Optional.of(categoria));
        when(livroRepository.save(any(Livro.class))).thenAnswer(invocation -> invocation.getArgument(0));

        LivroResponseDTO resultado = livroService.atualizarLivro(requestAtualizacao, livroIdExistente);

        assertNotNull(resultado);
        assertEquals(livroIdExistente, resultado.id());
        assertEquals(requestAtualizacao.nome(), resultado.nome());
        assertEquals(requestAtualizacao.autor(), resultado.autor());
        assertEquals(requestAtualizacao.quantidade(), resultado.quantidade());
        assertEquals(requestAtualizacao.status(), resultado.status());
        assertEquals(categoria.getNome(), resultado.categoria());

        verify(livroRepository, times(1)).findById(livroIdExistente);
        verify(categoriaRepository, times(1)).findById(requestAtualizacao.categoriaId());
        verify(livroRepository, times(1)).save(any(Livro.class));
    }

    @Test
    void atualizarLivro_QuandoLivroNaoEncontrado_DeveLancarLivroNaoEncontradoException() {
        Long livroIdInexistente = 99L;
        when(livroRepository.findById(livroIdInexistente)).thenReturn(Optional.empty());

        assertThrows(LivroNaoEncontradoException.class, () -> {
            livroService.atualizarLivro(livroRequestDTO, livroIdInexistente);
        });

        verify(livroRepository, times(1)).findById(livroIdInexistente);
        verify(categoriaRepository, never()).findById(anyLong());
        verify(livroRepository, never()).save(any(Livro.class));
    }

    @Test
    void atualizarLivro_QuandoCategoriaNaoEncontrada_DeveLancarCategoriaNaoEncontradaException() {
        Long livroIdExistente = 1L;
        Long idCategoriaInexistente = 99L;
        LivroRequestDTO requestComCategoriaInvalida = new LivroRequestDTO(
                "Livro Teste", "Autor", 1, StatusLivro.DISPONIVEL, idCategoriaInexistente
        );

        when(livroRepository.findById(livroIdExistente)).thenReturn(Optional.of(livro));
        when(categoriaRepository.findById(idCategoriaInexistente)).thenReturn(Optional.empty());

        assertThrows(CategoriaNaoEncontradaException.class, () -> {
            livroService.atualizarLivro(requestComCategoriaInvalida, livroIdExistente);
        });

        verify(livroRepository, times(1)).findById(livroIdExistente);
        verify(categoriaRepository, times(1)).findById(idCategoriaInexistente);
        verify(livroRepository, never()).save(any(Livro.class));
    }

    // --- Testes para removerLivro ---
    @Test
    void removerLivro_QuandoLivroExiste_DeveRetornarNoContent() {
        Long livroIdExistente = 1L;
        when(livroRepository.existsById(livroIdExistente)).thenReturn(true);
        doNothing().when(livroRepository).deleteById(livroIdExistente);

        ResponseEntity<Void> resposta = livroService.removerLivro(livroIdExistente);

        assertEquals(HttpStatus.NO_CONTENT, resposta.getStatusCode());
        verify(livroRepository, times(1)).existsById(livroIdExistente);
        verify(livroRepository, times(1)).deleteById(livroIdExistente);
    }

    @Test
    void removerLivro_QuandoLivroNaoExiste_DeveLancarLivroNaoEncontradoException() {
        Long livroIdInexistente = 99L;
        when(livroRepository.existsById(livroIdInexistente)).thenReturn(false);

        assertThrows(LivroNaoEncontradoException.class, () -> {
            livroService.removerLivro(livroIdInexistente);
        });

        verify(livroRepository, times(1)).existsById(livroIdInexistente);
        verify(livroRepository, never()).deleteById(anyLong());
    }

    // --- Testes para buscarLivroPorNome ---
    @Test
    void buscarLivroPorNome_QuandoLivrosEncontrados_DeveRetornarListaDeLivrosDTO() {
        String nomeBusca = "Duna";
        when(livroRepository.findByNomeContaining(nomeBusca)).thenReturn(List.of(livro));

        List<LivroResponseDTO> resultado = livroService.buscarLivroPorNome(nomeBusca);

        assertNotNull(resultado);
        assertFalse(resultado.isEmpty());
        assertEquals(1, resultado.size());
        assertTrue(resultado.getFirst().nome().contains(nomeBusca));
        verify(livroRepository, times(1)).findByNomeContaining(nomeBusca);
    }

    @Test
    void buscarLivroPorNome_QuandoNenhumLivroEncontrado_DeveLancarNenhumLivroEncontradoException() {
        String nomeBusca = "Inexistente";
        when(livroRepository.findByNomeContaining(nomeBusca)).thenReturn(Collections.emptyList());

        assertThrows(NenhumLivroEncontradoException.class, () -> {
            livroService.buscarLivroPorNome(nomeBusca);
        });
        verify(livroRepository, times(1)).findByNomeContaining(nomeBusca);
    }

    // --- Testes para filtrarPorCategoria ---
    @Test
    void filtrarPorCategoria_QuandoLivrosEncontrados_DeveRetornarListaDeLivrosDTO() {
        List<Long> idsCategoria = List.of(categoria.getId());
        when(livroRepository.findByCategoriaIdIn(idsCategoria)).thenReturn(List.of(livro));

        List<LivroResponseDTO> resultado = livroService.filtrarPorCategoria(idsCategoria);

        assertNotNull(resultado);
        assertFalse(resultado.isEmpty());
        assertEquals(1, resultado.size());
        assertEquals(categoria.getNome(), resultado.getFirst().categoria());
        verify(livroRepository, times(1)).findByCategoriaIdIn(idsCategoria);
    }

    @Test
    void filtrarPorCategoria_QuandoNenhumaCategoriaInformada_DeveLancarExcecaoOuRetornarVazio() {
        List<Long> idsCategoriaVazia = Collections.emptyList();
        when(livroRepository.findByCategoriaIdIn(idsCategoriaVazia)).thenReturn(Collections.emptyList());

        assertThrows(NenhumLivroEncontradoException.class, () -> {
            livroService.filtrarPorCategoria(idsCategoriaVazia);
        });
        verify(livroRepository, times(1)).findByCategoriaIdIn(idsCategoriaVazia);
    }

    @Test
    void filtrarPorCategoria_QuandoNenhumLivroEncontradoParaCategorias_DeveLancarNenhumLivroEncontradoException() {
        List<Long> idsCategoria = List.of(98L, 99L); // IDs de categorias que não têm livros
        when(livroRepository.findByCategoriaIdIn(idsCategoria)).thenReturn(Collections.emptyList());

        assertThrows(NenhumLivroEncontradoException.class, () -> {
            livroService.filtrarPorCategoria(idsCategoria);
        });
        verify(livroRepository, times(1)).findByCategoriaIdIn(idsCategoria);
    }


}