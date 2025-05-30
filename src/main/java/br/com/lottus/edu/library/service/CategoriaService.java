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
        if (categoriaRepository.existsByNome(categoria.getNome())) {
            throw new CategoriaJaExistenteException();
        }

        return categoriaRepository.save(categoria);
    }

    public void removerCategoria(Long id) {
        if (!categoriaRepository.existsById(id)) {
            throw new CategoriaNaoEncontradaException();
        }

        categoriaRepository.deleteById(id);
    }

}
