package br.com.lottus.edu.library.controller;

import br.com.lottus.edu.library.dto.CategoriaDTO;
import br.com.lottus.edu.library.model.Categoria;
import br.com.lottus.edu.library.service.CategoriaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@Tag(name = "Empréstimos", description = "Endpoint para gerenciamento dos empréstimos")
@RestController
@RequestMapping("/categorias")
public class CategoriaController {

    private final CategoriaService categoriaService;

    public CategoriaController(CategoriaService categoriaService) {
        this.categoriaService = categoriaService;
    }

    @Operation(summary = "Lista todas as categorias", description = "Retorna uma lista de todas as categorias")
    @GetMapping
    public ResponseEntity<List<CategoriaDTO>> listarTodas() {
        List<CategoriaDTO> categorias = categoriaService.listarCategorias();
        return ResponseEntity.ok(categorias);
    }

    @Operation(summary = "Adiciona uma nova categoria", description = "Retorna um status created")
    @PostMapping
    public ResponseEntity<Categoria> adicionarCategoria(@Valid @RequestBody Categoria categoria) {
        categoriaService.adicionarCategoria(categoria);
        return ResponseEntity.status(HttpStatus.CREATED).body(categoria);
    }

    @Operation(summary = "Remove uma categoria", description = "Retorna uma mensagem informando o resultado da operação")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removerCategoria(@PathVariable Long id) {
        categoriaService.removerCategoria(id);

        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Atualiza uma categoria existente", description = "Retorna a categoria atualizada")
    @PutMapping("/{id}")
    public ResponseEntity<Categoria> editarCategoria(@PathVariable Long id, @RequestBody Categoria categoria) {
        Categoria categoriaAtualizada = categoriaService.editarCategoria(id, categoria);
        return ResponseEntity.ok(categoriaAtualizada);
    }

    @PostMapping("/obter-ou-criar")
    public ResponseEntity<Categoria> obterOuCriarCategoria(@RequestBody CategoriaDTO categoriaDTO) {
        Categoria categoria = categoriaService.obterOuCriarCategoria(categoriaDTO);
        return ResponseEntity.ok(categoria);
    }
}
