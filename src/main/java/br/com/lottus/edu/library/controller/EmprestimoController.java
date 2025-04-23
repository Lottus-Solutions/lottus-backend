package br.com.lottus.edu.library.controller;


import br.com.lottus.edu.library.dto.RequestEmprestimo;
import br.com.lottus.edu.library.exception.AlunoComEmprestimoException;
import br.com.lottus.edu.library.model.Emprestimo;
import br.com.lottus.edu.library.model.StatusEmprestimo;
import br.com.lottus.edu.library.repository.AlunoRepository;
import br.com.lottus.edu.library.repository.EmprestimoRepository;
import br.com.lottus.edu.library.security.CustomUserPrincipal;
import br.com.lottus.edu.library.service.EmprestimoService;
import br.com.lottus.edu.library.service.EmprestimoServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/emprestimos")
public class EmprestimoController {

    private final EmprestimoService emprestimoService;
    private final EmprestimoRepository emprestimoRepository;

    @Autowired
    private AlunoRepository alunoRepository;

    private static final Logger logger = LoggerFactory.getLogger(EmprestimoController.class);

    public EmprestimoController(EmprestimoService emprestimoService, EmprestimoRepository emprestimoRepository) {
        this.emprestimoService = emprestimoService;
        this.emprestimoRepository = emprestimoRepository;
    }

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


    @GetMapping
    public ResponseEntity<List<Emprestimo>> listarEmprestimos() {
        List<Emprestimo> emprestimos = emprestimoService.listarEmprestimos();
        return ResponseEntity.ok(emprestimos);
    }

    @PostMapping("/{id}/renovar")
    public ResponseEntity<Void> renovarEmprestimo(@PathVariable Long id) {
        Boolean sucesso = emprestimoService.renovarEmprestimo(id);
        if (sucesso) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
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

    @GetMapping("/buscar")
    public ResponseEntity<List<Emprestimo>> buscarEmprestimos(
            @RequestParam(required = false) Long livroId,
            @RequestParam(required = false) String matricula,
            @RequestParam(required = false, defaultValue = "false") Boolean apenasAtrasados) {

        logger.info("Buscando empréstimos - Livro ID: {}, Matrícula: {}, Apenas Atrasados: {}", livroId, matricula, apenasAtrasados);

        List<Emprestimo> emprestimos = emprestimoService.buscarEmprestimos(livroId, matricula, apenasAtrasados);

        return ResponseEntity.ok(emprestimos);
    }

    @GetMapping("/historico/livro/{id}")
    public ResponseEntity<List<Emprestimo>> buscarHistoricoLivro(@PathVariable Long id) {
        List<Emprestimo> historico = emprestimoService.buscarHistoricoLivro(id);
        return ResponseEntity.ok(historico);
    }

    @GetMapping("/historico/aluno/{matricula}")
    public ResponseEntity<List<Emprestimo>> buscarHistoricoAluno(@PathVariable String matricula) {
        List<Emprestimo> historico = emprestimoService.buscarHistoricoAluno(matricula);
        return ResponseEntity.ok(historico);
    }

    @GetMapping("/atrasados")
    public ResponseEntity<List<Emprestimo>> filtrarEmprestimosAtrasados() {
        List<Emprestimo> atrasados = emprestimoService.filtrarEmprestimosAtrasados();
        return ResponseEntity.ok(atrasados);
    }

}
