package br.com.lottus.edu.library.controller;


import br.com.lottus.edu.library.dto.EmprestimoResponseDTO;
import br.com.lottus.edu.library.dto.RequestEmprestimo;
import br.com.lottus.edu.library.dto.VerificarRenovadoResponse;
import br.com.lottus.edu.library.model.Emprestimo;
import br.com.lottus.edu.library.repository.AlunoRepository;
import br.com.lottus.edu.library.repository.EmprestimoRepository;
import br.com.lottus.edu.library.service.EmprestimoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.data.domain.Page;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@Tag(name = "Empréstimos",description = "Endpoint para gerenciamento dos empréstimos")
@RestController
@RequestMapping("/emprestimos")
public class EmprestimoController {

    private final EmprestimoService emprestimoService;

    @Autowired
    private AlunoRepository alunoRepository;

    private static final Logger logger = LoggerFactory.getLogger(EmprestimoController.class);

    public EmprestimoController(EmprestimoService emprestimoService, EmprestimoRepository emprestimoRepository) {
        this.emprestimoService = emprestimoService;
    }

    @Operation(summary = "Realiza um empréstimo", description = "Retorna o empréstimo realizado")
    @PostMapping
    public ResponseEntity<Emprestimo> fazerEmprestimo(@RequestBody RequestEmprestimo requestEmprestimo) {

        Optional<Emprestimo> emprestimo = emprestimoService.fazerEmprestimo(requestEmprestimo);

        if (emprestimo.isPresent()) {
            Emprestimo emprestimoOfc = emprestimo.get();
            URI location = ServletUriComponentsBuilder
                    .fromCurrentRequest()
                    .path("/{id}")
                    .buildAndExpand(emprestimoOfc.getId())
                    .toUri();

            return ResponseEntity.created(location).body(emprestimoOfc);
        } else {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(null);
        }
    }


    @Operation(summary = "Lista todos os empréstimos", description = "Retorna uma lista de todos os empréstimos")
    @GetMapping
    public ResponseEntity<Page<EmprestimoResponseDTO>> listarEmprestimos(
            @RequestParam(required = false) String busca,
            @RequestParam(required = false) Boolean atrasados,
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "10") int tamanho
    ) {
        Pageable pageable = PageRequest.of(pagina, tamanho);
        boolean filtroAtrasados = atrasados != null && atrasados;
        Page<EmprestimoResponseDTO> emprestimos = emprestimoService.listarEmprestimos(busca, filtroAtrasados, pageable);
        return ResponseEntity.ok(emprestimos);
    }

    @PostMapping("/{id}/renovar")
    public ResponseEntity<Boolean> renovarEmprestimo(@PathVariable Long id) {
         return ResponseEntity.ok(emprestimoService.renovarEmprestimo(id));
    }

    @PostMapping("/{id}/finalizar")
    public ResponseEntity<Boolean> finalizarEmprestimo(@PathVariable Long id) {
        Boolean sucesso = emprestimoService.finalizarEmprestimo(id);
        if (sucesso) {
            return ResponseEntity.ok(true);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{id}/verificar-quantidade-renovado")
    public ResponseEntity<VerificarRenovadoResponse> verificarQuantidadeRenovado(@PathVariable Long id) {

        return ResponseEntity.ok(emprestimoService.verificarQuantidadeRenovado(id));
    }

//    @GetMapping("/buscar")
//    public ResponseEntity<List<Emprestimo>> buscarEmprestimos(
//            @RequestParam(required = false) String valor){
//
//        List<Emprestimo> emprestimos = emprestimoService.buscarEmprestimos(valor);
//
//        return ResponseEntity.ok(emprestimos);
//    }

    @GetMapping("/historico/livro/{id}")
    public ResponseEntity<List<Emprestimo>> buscarHistoricoLivro(@PathVariable Long id) {
        List<Emprestimo> historico = emprestimoService.buscarHistoricoLivro(id);
        return ResponseEntity.ok(historico);
    }

    @GetMapping("/historico/aluno/{matricula}")
    public ResponseEntity<List<Emprestimo>> buscarHistoricoAluno(@PathVariable Long matricula) {
        List<Emprestimo> historico = emprestimoService.buscarHistoricoAluno(matricula);
        return ResponseEntity.ok(historico);
    }

    @GetMapping("/atrasados")
    public ResponseEntity<List<Emprestimo>> filtrarEmprestimosAtrasados() {
        List<Emprestimo> atrasados = emprestimoService.filtrarEmprestimosAtrasados();
        return ResponseEntity.ok(atrasados);
    }

}
