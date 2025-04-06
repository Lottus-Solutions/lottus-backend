package br.com.lottus.edu.library.controller;

import br.com.lottus.edu.library.dto.LivroRequestDTO;
import br.com.lottus.edu.library.dto.LivroResponseDTO;
import br.com.lottus.edu.library.repository.CategoriaRepository;
import br.com.lottus.edu.library.service.LivroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/livros")
public class LivroController {

    @Autowired
    private LivroService livroService;

    @GetMapping
    public ResponseEntity<List<LivroResponseDTO>> buscarTodos() {
        List<LivroResponseDTO> livros = livroService.buscarTodos();
        return ResponseEntity.ok(livros);
    }

    @PostMapping
    public ResponseEntity<LivroResponseDTO> adicionarLivro(@RequestBody LivroRequestDTO livroDTO) {
        LivroResponseDTO response = livroService.cadastrarLivro(livroDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<LivroResponseDTO> atualizarLivro(@RequestBody LivroRequestDTO livroRequestDTO, @PathVariable Long id) {
       LivroResponseDTO response = livroService.atualizarLivro(livroRequestDTO, id);
       return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> removerLivro(@PathVariable Long id) {
        livroService.removerLivro(id);
        return ResponseEntity.status(200).body("Livro removido com sucesso");
    }

    @GetMapping("/buscar/{nome}")
    public ResponseEntity<List<LivroResponseDTO>> buscarLivroPorNome(@PathVariable String nome) {
        List<LivroResponseDTO> livros = livroService.buscarLivroPorNome(nome);
        return ResponseEntity.ok(livros);
    }
}
