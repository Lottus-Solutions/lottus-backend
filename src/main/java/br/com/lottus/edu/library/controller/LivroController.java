package br.com.lottus.edu.library.controller;

import br.com.lottus.edu.library.dto.LivroRequestDTO;
import br.com.lottus.edu.library.dto.LivroResponseDTO;
import br.com.lottus.edu.library.model.Categoria;
import br.com.lottus.edu.library.service.CategoriaService;
import br.com.lottus.edu.library.service.LivroService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/livros")
public class LivroController {

    private final LivroService livroService;
    private CategoriaService categoriaService;

    public LivroController(LivroService livroService, CategoriaService categoriaService) {
        this.livroService = livroService;
        this.categoriaService = categoriaService;
    }

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

    @GetMapping("/filtrar-por-categoria")
    public ResponseEntity<List<LivroResponseDTO>> filtrarLivroPorCategoria(@RequestParam List<Long> categoriaIds) {
        List<LivroResponseDTO> livros = livroService.filtrarPorCategoria(categoriaIds);
        return ResponseEntity.ok(livros);
    }
}
