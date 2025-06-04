package br.com.lottus.edu.library.service;

import br.com.lottus.edu.library.dto.CategoriaDTO;
import br.com.lottus.edu.library.exception.CategoriaJaExistenteException;
import br.com.lottus.edu.library.exception.CategoriaNaoEncontradaException;
import br.com.lottus.edu.library.model.Categoria;
import br.com.lottus.edu.library.repository.CategoriaRepository;
import br.com.lottus.edu.library.repository.LivroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoriaService {

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private LivroRepository livroRepository;

    public List<CategoriaDTO> listarCategorias() {

        List<Categoria> categorias = categoriaRepository.findAll();

        return categorias.stream()
                .map(this::converterParaDTO)
                .toList();
    }

    public Categoria adicionarCategoria(Categoria categoria) {
        if (categoriaRepository.existsByNome(categoria.getNome())) {
            throw new CategoriaJaExistenteException();
        }

        return categoriaRepository.save(categoria);
    }

    public void removerCategoria(Long id) {
        if (!categoriaExiste(id)) {
            throw new CategoriaNaoEncontradaException();
        }

        categoriaRepository.deleteById(id);
    }

    public Categoria editarCategoria(Long id, Categoria categoriaRequest) {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(CategoriaNaoEncontradaException::new);

        List<Categoria> categorias = categoriaRepository.findAll();

        categoria.setNome(categoriaRequest.getNome());
        categoria.setCor(categoriaRequest.getCor());
        return categoriaRepository.save(categoria);
    }

    private boolean categoriaExiste(Long id)  {
        return categoriaRepository.existsById(id);
    }

    public CategoriaDTO converterParaDTO(Categoria categoria) {
        Integer qtdLivrosCadastrados = livroRepository.countByCategoriaId(categoria.getId());

        return new CategoriaDTO(categoria.getId(), categoria.getNome(), categoria.getCor(), qtdLivrosCadastrados);
    }

}
