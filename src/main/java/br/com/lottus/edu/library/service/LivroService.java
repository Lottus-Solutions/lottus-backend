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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class LivroService {

    private static final Logger log = LoggerFactory.getLogger(LivroService.class);

    @Autowired
    private LivroRepository livroRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    public Page<LivroResponseDTO> listarTodos(int pagina, int tamanho) {

        Pageable pagable = PageRequest.of(pagina, tamanho, Sort.by("id").descending());

        Page<Livro> paginadeLivros =  livroRepository.findAll(pagable);

        return paginadeLivros.map(this::converterParaDTO);
    }



    public LivroResponseDTO cadastrarLivro(LivroRequestDTO livroRequestDTO) {
        Categoria categoria = categoriaRepository.findById(livroRequestDTO.categoriaId())
                .orElseThrow(CategoriaNaoEncontradaException::new);

        Livro livro = new Livro();
        livro.setNome(livroRequestDTO.nome());
        livro.setAutor(livroRequestDTO.autor());
        livro.setQuantidade(livroRequestDTO.quantidade());
        livro.setQuantidadeDisponivel(livroRequestDTO.quantidade());
        livro.setStatus(StatusLivro.DISPONIVEL);
        livro.setCategoria(categoria);
        livro.setDescricao(livroRequestDTO.descricao());
        livro = livroRepository.save(livro);

        return new LivroResponseDTO(livro.getId(), livro.getNome(), livro.getAutor(), livro.getQuantidade(), livro.getQuantidadeDisponivel(),livro.getStatus(), livro.getCategoria().getNome(), livro.getDescricao());
    }

    public LivroResponseDTO atualizarLivro(LivroRequestDTO livroRequestDTO, Long id) {
        Livro livro = livroRepository.findById(id)
                .orElseThrow(LivroNaoEncontradoException::new);

        Categoria categoria = categoriaRepository.findById(livroRequestDTO.categoriaId())
                .orElseThrow(CategoriaNaoEncontradaException::new);

        livro.setNome(livroRequestDTO.nome());
        livro.setAutor(livroRequestDTO.autor());
        livro.setQuantidade(livroRequestDTO.quantidade());
        livro.setCategoria(categoria);
        livro.setDescricao(livroRequestDTO.descricao());

        livro = livroRepository.save(livro);

        return new LivroResponseDTO(livro.getId(), livro.getNome(), livro.getAutor(), livro.getQuantidade(), livro.getQuantidadeDisponivel(), livro.getStatus(), livro.getCategoria().getNome(), livro.getDescricao());
    }

    public ResponseEntity<Void> removerLivro(Long id) {
        if (!livroRepository.existsById(id)) {
            throw new LivroNaoEncontradoException();
        }

        livroRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @Transactional(readOnly = true) //Melhora a performance
    public Page<LivroResponseDTO> buscarLivro(String valor, String status, Long categoriaId, int pagina, int tamanho) {

        Pageable pageable = PageRequest.of(pagina, tamanho, Sort.by("id").descending());

        String termoBusca = valor == null ? "" : valor.trim();

        StatusLivro statusLivro = StatusLivro.fromString(status);

        return livroRepository.findByBuscaOuFiltro(termoBusca, statusLivro, categoriaId, pageable);
    }

    public List<LivroResponseDTO> filtrarPorCategoria(List<Long> categoriaIds) {
        List<LivroResponseDTO> livros = livroRepository.findByCategoriaIdIn(categoriaIds).stream()
                .map(livro -> new LivroResponseDTO(
                        livro.getId(),
                        livro.getNome(),
                        livro.getAutor(),
                        livro.getQuantidade(),
                        livro.getQuantidadeDisponivel(),
                        livro.getStatus(),
                        livro.getCategoria().getNome(),
                        livro.getDescricao()))
                .toList();

        if (livros.isEmpty()) {
            throw new NenhumLivroEncontradoException();
        }

        return livros;
    }

        public Page<LivroResponseDTO> filtrarPorStatus(String status, int pagina, int tamanho) {
        StatusLivro statusLivro = StatusLivro.fromString(status);

        Pageable pageable = PageRequest.of(pagina, tamanho, Sort.by("id").descending());

        Page<Livro> livrosPage = livroRepository.findByStatus(statusLivro, pageable);
        Page<LivroResponseDTO> livros = livrosPage.map(this::converterParaDTO);

        if (livros.isEmpty()) {
            throw new NenhumLivroEncontradoException();
        }

        return livros;
    }

    private LivroResponseDTO converterParaDTO(Livro livro) {

        return new LivroResponseDTO(
                livro.getId(),
                livro.getNome(),
                livro.getAutor(),
                livro.getQuantidade(),
                livro.getQuantidadeDisponivel(),
                livro.getStatus(),
                livro.getCategoria().getNome(),
                livro.getDescricao()
        );
    }


}
