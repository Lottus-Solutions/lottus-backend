package br.com.lottus.edu.library.controller;

import br.com.lottus.edu.library.model.Turma;
import br.com.lottus.edu.library.service.TurmaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Turmas", description = "Endpoint para o gerenciamento de turmas")
@RestController
@RequestMapping("/turmas")
public class TurmaController {

    private final TurmaService turmaService;

    public TurmaController(TurmaService turmaService) {
        this.turmaService = turmaService;
    }

    @Operation(summary = "Lista todas as turmas", description = "Retorna uma lista de todas as turmas")
    @GetMapping
    public ResponseEntity<List<Turma>> listarTurmas() {
        List<Turma> turmas = turmaService.listarTurmas();
        return ResponseEntity.ok(turmas);
    }

    @Operation(summary = "Adiciona uma nova turma", description = "Retorna a turma cadastrada com um status created")
    @PostMapping
    public ResponseEntity<Turma> adicionarTurma(@RequestBody Turma turma) {
        turmaService.adicionarTurma(turma);
        return ResponseEntity.status(HttpStatus.CREATED).body(turma);
    }

    @Operation(summary = "Atualiza uma turma existente", description = "Retorna a turma atualizada")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> removerTurma(@PathVariable Long id) {
        turmaService.removerTurma(id);
        return ResponseEntity.status(HttpStatus.OK).body("Turma removida com sucesso");
    }

    @Operation(summary = "Atualiza uma turma existente", description = "Retorna a turma atualizada")
    @PutMapping("/{matricula}")
    public ResponseEntity<Turma> editarTurma(@PathVariable Long matricula, @RequestBody Turma turma) {
        Turma turmaAtualizada = turmaService.editarTurma(matricula, turma);
        return ResponseEntity.status(HttpStatus.OK).body(turmaAtualizada);
    }
}
