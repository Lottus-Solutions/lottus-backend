package br.com.lottus.edu.library.controller;

import br.com.lottus.edu.library.dto.LivroRequestDTO;
import br.com.lottus.edu.library.dto.LivroResponseDTO;
import br.com.lottus.edu.library.model.Categoria;
import br.com.lottus.edu.library.service.CategoriaService;
import br.com.lottus.edu.library.service.LivroService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Livros", description = "Endpoint para o gerencimaento de livros")
@RestController
@RequestMapping("/livros")
public class LivroController {

    private final LivroService livroService;
    private CategoriaService categoriaService;

    public LivroController(LivroService livroService, CategoriaService categoriaService) {
        this.livroService = livroService;
        this.categoriaService = categoriaService;
    }
    @Operation(summary = "Busca todos os livros", description = "Retorna uma lista de todos os livros")
    @GetMapping
    public ResponseEntity<List<LivroResponseDTO>> buscarTodos() {
        List<LivroResponseDTO> livros = livroService.buscarTodos();
        return ResponseEntity.ok(livros);
    }

    @Operation(summary = "Adiciona um novo livro", description = "Retorna o livro cadastrado com um status created")
    @PostMapping
    public ResponseEntity<LivroResponseDTO> adicionarLivro(@RequestBody LivroRequestDTO livroDTO) {
        LivroResponseDTO response = livroService.cadastrarLivro(livroDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "atualiza um livro existente", description = "Retorna o livro atualizado")
    @PutMapping("/{id}")
    public ResponseEntity<LivroResponseDTO> atualizarLivro(@RequestBody LivroRequestDTO livroRequestDTO, @PathVariable Long id) {
       LivroResponseDTO response = livroService.atualizarLivro(livroRequestDTO, id);
       return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(summary = "Remove um livro", description = "Retorna uma mensagem informando o resultado da operação")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> removerLivro(@PathVariable Long id) {
        livroService.removerLivro(id);
        return ResponseEntity.status(200).body("Livro removido com sucesso");
    }

    @Operation(summary = "busca livros pelo nome", description = "Retorna uma lista de livros com o nome informado")
    @GetMapping("/buscar/{valor}")
    public ResponseEntity<List<LivroResponseDTO>> buscarLivro(@PathVariable String valor) {
        List<LivroResponseDTO> livros = livroService.buscarLivro(valor);
        return ResponseEntity.ok(livros);
    }

    @Operation(summary = "filtrar livros por categoria", description = "Retorna uma lista de livros filtrados pela categoria")
    @GetMapping("/filtrar-por-categoria")
    public ResponseEntity<List<LivroResponseDTO>> filtrarLivroPorCategoria(@RequestParam List<Long> categoriaIds) {
        List<LivroResponseDTO> livros = livroService.filtrarPorCategoria(categoriaIds);
        return ResponseEntity.ok(livros);
    }
}
