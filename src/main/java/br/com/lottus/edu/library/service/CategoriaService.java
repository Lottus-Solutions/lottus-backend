package br.com.lottus.edu.library.service;

import br.com.lottus.edu.library.exception.CategoriaJaExistenteException;
import br.com.lottus.edu.library.exception.CategoriaNaoEncontradaException;
import br.com.lottus.edu.library.model.Categoria;
import br.com.lottus.edu.library.repository.CategoriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoriaService {

    @Autowired
    private CategoriaRepository categoriaRepository;

    public List<Categoria> listarCategorias() {
        return categoriaRepository.findAll();
    }

    public Categoria adcionarCategoria(Categoria categoria) {
        if (categoriaExiste(categoria.getId())) {
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

        if (categorias.stream()
                .anyMatch(c -> c.getNome().equalsIgnoreCase(categoriaRequest.getNome()))) {
            throw new CategoriaJaExistenteException();
        }

        categoria.setNome(categoriaRequest.getNome());
        categoria.setCor(categoriaRequest.getCor());
        return categoriaRepository.save(categoria);
    }

    private boolean categoriaExiste(Long id)  {
        return categoriaRepository.existsById(id);
    }

}
