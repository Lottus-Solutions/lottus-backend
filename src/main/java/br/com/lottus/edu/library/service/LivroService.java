package br.com.lottus.edu.library.service;

import br.com.lottus.edu.library.model.Livro;
import br.com.lottus.edu.library.repository.LivroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LivroService {

    @Autowired
    private LivroRepository livroRepository;

    public List<Livro> buscarTodos() {
        return livroRepository.findAll();
    }

    public Livro cadastrarLivro(Livro livro) {
        return livroRepository.save(livro);
    }

    public ResponseEntity<Livro> atualizarLivro(Livro livro, Long id) {
        if (livroRepository.existsById(id)) {
            livro.setId(id);
            return ResponseEntity.ok(livroRepository.save(livro));
        }
        return ResponseEntity.notFound().build();
    }

    public ResponseEntity<Void> removerLivro(Long id) {
        if (livroRepository.existsById(id)) {
            livroRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

}
