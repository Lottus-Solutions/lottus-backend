package br.com.lottus.edu.library.controller;

import br.com.lottus.edu.library.dto.LivroRequestDTO;
import br.com.lottus.edu.library.dto.LivroResponseDTO;
import br.com.lottus.edu.library.model.Categoria;
import br.com.lottus.edu.library.model.Livro;
import br.com.lottus.edu.library.model.StatusLivro;
import br.com.lottus.edu.library.service.CategoriaService;
import br.com.lottus.edu.library.service.LivroService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
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
    @Operation(summary = "Busca todos os livros em paginação", description = "Retorna uma pagina de livros")
    @GetMapping
    public ResponseEntity<Page<LivroResponseDTO>> buscarTodos(
            @RequestParam(value = "pagina", defaultValue = "0") int pagina,
            @RequestParam(value = "tamanho", defaultValue = "10") int tamanho)

    {
        return ResponseEntity.ok(livroService.listarTodos(pagina, tamanho));

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

    @Operation(summary = "Busca livros pelo nome ou autor", description = "Retorna uma página de livros filtrados pelo nome ou autor informado")
    @GetMapping("/buscar")
    public ResponseEntity<Page<LivroResponseDTO>> buscarLivro(
            @RequestParam(value = "valor", required = false) String valor,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "categoriaId", required = false) Long categoriaId,
            @RequestParam(value = "pagina", defaultValue = "0") int pagina,
            @RequestParam(value = "tamanho", defaultValue = "10") int tamanho) {
        Page<LivroResponseDTO> livros = livroService.buscarLivro(valor, status, categoriaId, pagina, tamanho);
        return ResponseEntity.ok(livros);
    }

    @Operation(summary = "filtrar livros por categoria", description = "Retorna uma lista de livros filtrados pela categoria")
    @GetMapping("/filtrar-por-categoria")
    public ResponseEntity<List<LivroResponseDTO>> filtrarLivroPorCategoria(@RequestParam List<Long> categoriaIds) {
        List<LivroResponseDTO> livros = livroService.filtrarPorCategoria(categoriaIds);
        return ResponseEntity.ok(livros);
    }

    @Operation(summary = "filtrar livros por status", description = "Retorna uma lista de livros filtrados pelo status")
    @GetMapping("/filtrar-por-status")
    public ResponseEntity<Page<LivroResponseDTO>> filtrarPorStatus(@RequestParam(value = "statusvalue", required = true) String status,
                                                                   @RequestParam(value = "pagina", defaultValue="0")int pagina,
                                                                   @RequestParam(value = "tamanho", defaultValue="10")int tamanho) {
        Page<LivroResponseDTO> livros = livroService.filtrarPorStatus(status, pagina, tamanho);
        return ResponseEntity.ok(livros);
    }
}
