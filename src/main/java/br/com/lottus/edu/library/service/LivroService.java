package br.com.lottus.edu.library.service;

import br.com.lottus.edu.library.dto.LivroRequestDTO;
import br.com.lottus.edu.library.dto.LivroResponseDTO;
import br.com.lottus.edu.library.exception.*;
import br.com.lottus.edu.library.model.Categoria;
import br.com.lottus.edu.library.model.Livro;
import br.com.lottus.edu.library.model.StatusEmprestimo;
import br.com.lottus.edu.library.model.StatusLivro;
import br.com.lottus.edu.library.repository.CategoriaRepository;
import br.com.lottus.edu.library.repository.EmprestimoRepository;
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

    @Autowired
    private LivroRepository livroRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    EmprestimoRepository emprestimoRepository;

    public LivroResponseDTO cadastrarLivro(LivroRequestDTO livroRequestDTO) {
        if (livroJaCadastrado(livroRequestDTO.nome())) {
            throw new LivroJaCadastradoException();
        }

        Categoria categoria = buscarCategoria(livroRequestDTO.categoriaId());

        Livro livro = new Livro();
        livro.setNome(livroRequestDTO.nome());
        livro.setAutor(livroRequestDTO.autor());
        livro.setQuantidade(livroRequestDTO.quantidade());
        livro.setQuantidadeDisponivel(livroRequestDTO.quantidade());
        livro.setStatus(StatusLivro.DISPONIVEL);
        livro.setCategoria(categoria);
        livro.setDescricao(livroRequestDTO.descricao());
        livro = livroRepository.save(livro);

        return converterParaDTO(livroRepository.save(livro));
    }

    public LivroResponseDTO atualizarLivro(LivroRequestDTO livroRequestDTO, Long id) {
        Livro livro = buscarLivro(id);
        Categoria categoria = buscarCategoria(livroRequestDTO.categoriaId());

        livro.setNome(livroRequestDTO.nome());
        livro.setAutor(livroRequestDTO.autor());
        livro.setQuantidade(livroRequestDTO.quantidade());
        livro.setCategoria(categoria);
        livro.setDescricao(livroRequestDTO.descricao());

        return converterParaDTO(livroRepository.save(livro));
    }

    public ResponseEntity<Void> removerLivro(Long id) {
        if (!livroRepository.existsById(id)) {
            throw new LivroNaoEncontradoException();
        }

        List<StatusEmprestimo> statusAtivo = List.of(StatusEmprestimo.ATIVO);

        if(emprestimoRepository.existsByLivro_IdAndStatusEmprestimoIn(id, statusAtivo)){
            throw new LivroComEmprestimosAtivosException();
        }

        livroRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @Transactional(readOnly = true) //Melhora a performance
    public Page<LivroResponseDTO> buscarLivro(String termoBusca, String status, Long categoriaId, int pagina, int tamanho) {
        Pageable pageable = PageRequest.of(pagina, tamanho, Sort.by("id").descending());

        String termoNormalizado = (termoBusca == null || termoBusca.isBlank()) ? null : termoBusca.trim();
        List<StatusLivro> statusList = mapStatusParaLista(status);

        return livroRepository.findByBuscaOuFiltro(termoNormalizado, statusList, categoriaId, pageable);
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

    private List<StatusLivro> mapStatusParaLista(String status) {
        if (status == null || status.isBlank()) {
            return List.of(StatusLivro.values());
        }
        return List.of(StatusLivro.fromString(status.trim()));
    }

    private Livro buscarLivro(Long id)  {
        return livroRepository.findById(id)
                .orElseThrow(LivroNaoEncontradoException::new);
    }

    private Categoria buscarCategoria(Long id) {
        return categoriaRepository.findById(id)
                .orElseThrow(CategoriaInvalidaException::new);
    }

    private boolean livroJaCadastrado(String nome) {
        return livroRepository.existsByNomeIgnoreCase(nome);
    }
}