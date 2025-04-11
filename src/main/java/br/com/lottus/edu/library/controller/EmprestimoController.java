package br.com.lottus.edu.library.controller;


import br.com.lottus.edu.library.dto.RequestEmprestimo;
import br.com.lottus.edu.library.model.Emprestimo;
import br.com.lottus.edu.library.security.CustomUserPrincipal;
import br.com.lottus.edu.library.service.EmprestimoService;
import br.com.lottus.edu.library.service.EmprestimoServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.LocalDate;
import java.util.Optional;

@RestController
@RequestMapping("/emprestimos")
public class EmprestimoController {

    private final EmprestimoService emprestimoService;
    private static final Logger logger = LoggerFactory.getLogger(EmprestimoController.class);

    public EmprestimoController(EmprestimoService emprestimoService) {
        this.emprestimoService = emprestimoService;
    }

    @PostMapping
    public ResponseEntity<Emprestimo> fazerEmprestimo(@RequestBody RequestEmprestimo requestEmprestimo){
        logger.info("=== INÍCIO DO PROCESSAMENTO DE EMPRÉSTIMO ===");
        logger.info("Dados da requisição: {}", requestEmprestimo);
        
        // Verificar autenticação
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            logger.info("Usuário autenticado: {}", authentication.getName());
            
            if (authentication.getPrincipal() instanceof CustomUserPrincipal) {
                CustomUserPrincipal userPrincipal = (CustomUserPrincipal) authentication.getPrincipal();
                logger.info("ID do usuário: {}, Email: {}", userPrincipal.getUserId(), userPrincipal.getEmail());
            } else {
                logger.warn("Principal não é do tipo CustomUserPrincipal: {}", authentication.getPrincipal().getClass().getName());
            }
        } else {
            logger.error("Usuário não está autenticado!");
        }

        Optional<Emprestimo> emprestimo = emprestimoService.fazerEmprestimo(requestEmprestimo);

        if(emprestimo.isPresent()){
            Emprestimo emprestimoOfc = emprestimo.get();
            URI location = ServletUriComponentsBuilder
                    .fromCurrentRequest()
                    .path("/{id}")
                    .buildAndExpand(emprestimoOfc.getId())
                    .toUri();

            logger.info("Empréstimo realizado com sucesso. ID: {}", emprestimoOfc.getId());
            return ResponseEntity.created(location).body(emprestimoOfc);
        } else {
            logger.error("Falha ao realizar empréstimo. Dados inválidos ou inconsistentes.");
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                    .body(null);
        }
    }
}
