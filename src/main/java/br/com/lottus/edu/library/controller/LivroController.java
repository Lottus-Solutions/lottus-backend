package br.com.lottus.edu.library.controller;

import br.com.lottus.edu.library.model.Livro;
import br.com.lottus.edu.library.service.LivroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/livros")
public class LivroController {

    @Autowired
    private LivroService livroService;

    @GetMapping
    public ResponseEntity<List<Livro>> listarLivros() {
        return ResponseEntity.ok(livroService.buscarTodos());
    }

    @PostMapping
    public ResponseEntity<String> adicionarLivro(@RequestBody Livro livro) {
        livroService.cadastrarLivro(livro);
        return ResponseEntity.status(201).body("Livro cadastrado com sucesso");
    }

    @PutMapping("/{id}")
    public ResponseEntity<Livro> atualizarLivro(@RequestBody Livro livro, @PathVariable Long id) {
       return livroService.atualizarLivro(livro, id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> removerLivro(@PathVariable Long id) {
        livroService.removerLivro(id);
        return ResponseEntity.status(200).body("Livro removido com sucesso");
    }
}
