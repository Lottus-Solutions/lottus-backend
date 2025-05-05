package br.com.lottus.edu.library.controller;

import br.com.lottus.edu.library.model.Categoria;
import br.com.lottus.edu.library.service.CategoriaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categorias")
public class CategoriaController {

    private final CategoriaService categoriaService;

    public CategoriaController(CategoriaService categoriaService) {
        this.categoriaService = categoriaService;
    }

    @GetMapping
    public ResponseEntity<List<Categoria>> listarTodas() {
        List<Categoria> categorias = categoriaService.listarCategorias();
        return ResponseEntity.ok(categorias);
    }

    @PostMapping
    public ResponseEntity<Categoria> adicionarCategoria(@RequestBody Categoria categoria) {
        categoriaService.adcionarCategoria(categoria);
        return ResponseEntity.status(HttpStatus.CREATED).body(categoria);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> removerCategoria(@PathVariable Long id) {
        categoriaService.removerCategoria(id);
        return ResponseEntity.status(HttpStatus.OK).body("Categoria removida com sucesso!");
    }
}
