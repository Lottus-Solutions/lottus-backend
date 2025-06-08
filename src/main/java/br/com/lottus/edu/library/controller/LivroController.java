package br.com.lottus.edu.library.controller;

import br.com.lottus.edu.library.dto.LivroRequestDTO;
import br.com.lottus.edu.library.dto.LivroResponseDTO;
import br.com.lottus.edu.library.service.CategoriaService;
import br.com.lottus.edu.library.service.LivroService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Livros", description = "Endpoint para o gerenciamento de livros")
@RestController
@RequestMapping("/livros")
public class LivroController {

    private final LivroService livroService;
    private final CategoriaService categoriaService;

    public LivroController(LivroService livroService, CategoriaService categoriaService) {
        this.livroService = livroService;
        this.categoriaService = categoriaService;
    }

    @Operation(summary = "Adiciona um novo livro", description = "Retorna o livro cadastrado com um status created")
    @PostMapping
    public ResponseEntity<LivroResponseDTO> adicionarLivro(@Valid @RequestBody LivroRequestDTO livroDTO) {
        LivroResponseDTO response = livroService.cadastrarLivro(livroDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "atualiza um livro existente", description = "Retorna o livro atualizado")
    @PutMapping("/{id}")
    public ResponseEntity<LivroResponseDTO> atualizarLivro(@Valid @RequestBody LivroRequestDTO livroRequestDTO, @PathVariable Long id) {
       LivroResponseDTO response = livroService.atualizarLivro(livroRequestDTO, id);
       return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(summary = "Remove um livro", description = "Retorna uma mensagem informando o resultado da operação")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removerLivro(@PathVariable Long id) {
        livroService.removerLivro(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Busca livros com filtros opcionais (nome, autor, status, categoria)", description = "Retorna uma página de livros com base nos filtros informados.")
    @GetMapping()
    public ResponseEntity<Page<LivroResponseDTO>> buscar(
            @RequestParam(value = "valor", required = false) String valor,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "categoriaId", required = false) Long categoriaId,
            @RequestParam(value = "pagina", defaultValue = "0") int pagina,
            @RequestParam(value = "tamanho", defaultValue = "10") int tamanho) {

        Page<LivroResponseDTO> livros = livroService.buscarLivro(valor, status, categoriaId, pagina, tamanho);
        return ResponseEntity.ok(livros);
    }
}
