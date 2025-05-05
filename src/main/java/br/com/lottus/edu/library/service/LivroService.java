package br.com.lottus.edu.library.service;

import br.com.lottus.edu.library.dto.LivroRequestDTO;
import br.com.lottus.edu.library.dto.LivroResponseDTO;
import br.com.lottus.edu.library.exception.CategoriaNaoEncontradaException;
import br.com.lottus.edu.library.exception.LivroNaoEncontradoException;
import br.com.lottus.edu.library.exception.NenhumLivroEncontradoException;
import br.com.lottus.edu.library.model.Categoria;
import br.com.lottus.edu.library.model.Livro;
import br.com.lottus.edu.library.repository.CategoriaRepository;
import br.com.lottus.edu.library.repository.LivroRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class LivroService {

    private static final Logger log = LoggerFactory.getLogger(LivroService.class);

    @Autowired
    private LivroRepository livroRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    public List<LivroResponseDTO> buscarTodos() {
        return livroRepository.findAll().stream()
                .map(livro -> new LivroResponseDTO(
                        livro.getId(),
                        livro.getNome(),
                        livro.getAutor(),
                        livro.getQuantidade(),
                        livro.getStatus(),
                        livro.getCategoria().getNome()))
                .toList();
    }

    public LivroResponseDTO cadastrarLivro(LivroRequestDTO livroRequestDTO) {
        Categoria categoria = categoriaRepository.findById(livroRequestDTO.categoriaId())
                .orElseThrow(CategoriaNaoEncontradaException::new);

        Livro livro = new Livro();
        livro.setNome(livroRequestDTO.nome());
        livro.setAutor(livroRequestDTO.autor());
        livro.setQuantidade(livroRequestDTO.quantidade());
        livro.setStatus(livroRequestDTO.status());
        livro.setCategoria(categoria);
        livro = livroRepository.save(livro);

        return new LivroResponseDTO(livro.getId(), livro.getNome(), livro.getAutor(), livro.getQuantidade(), livro.getStatus(), livro.getCategoria().getNome());
    }

    public LivroResponseDTO atualizarLivro(LivroRequestDTO livroRequestDTO, Long id) {
        Livro livro = livroRepository.findById(id)
                .orElseThrow(() -> new LivroNaoEncontradoException());

        Categoria categoria = categoriaRepository.findById(livroRequestDTO.categoriaId())
                .orElseThrow(() -> new CategoriaNaoEncontradaException());

        livro.setNome(livroRequestDTO.nome());
        livro.setAutor(livroRequestDTO.autor());
        livro.setQuantidade(livroRequestDTO.quantidade());
        livro.setStatus(livroRequestDTO.status());
        livro.setCategoria(categoria);

        livro = livroRepository.save(livro);

        return new LivroResponseDTO(livro.getId(), livro.getNome(), livro.getAutor(), livro.getQuantidade(), livro.getStatus(), livro.getCategoria().getNome());
    }

    public ResponseEntity<Void> removerLivro(Long id) {
        if (!livroRepository.existsById(id)) {
            throw new LivroNaoEncontradoException();
        }

        livroRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    public List<LivroResponseDTO> buscarLivroPorNome(String nome) {
        List<LivroResponseDTO> livrosEncontrados = livroRepository.findByNomeContaining(nome)
                .stream()
                .map(livro -> new LivroResponseDTO(
                livro.getId(),
                livro.getNome(),
                livro.getAutor(),
                livro.getQuantidade(),
                livro.getStatus(),
                livro.getCategoria().getNome()))
                .toList();

        if (livrosEncontrados.isEmpty()) {
            throw new NenhumLivroEncontradoException();
        }

        return livrosEncontrados;
    }

    public List<LivroResponseDTO> filtrarPorCategoria(List<Long> categoriaIds) {
        List<LivroResponseDTO> livros = livroRepository.findByCategoriaIdIn(categoriaIds).stream()
                .map(livro -> new LivroResponseDTO(
                        livro.getId(),
                        livro.getNome(),
                        livro.getAutor(),
                        livro.getQuantidade(),
                        livro.getStatus(),
                        livro.getCategoria().getNome()))
                .toList();

        if (livros.isEmpty()) {
            throw new NenhumLivroEncontradoException();
        }

        return livros;
    }

}
