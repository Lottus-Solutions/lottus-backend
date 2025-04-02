package br.com.lottus.edu.library.controller;

import br.com.lottus.edu.library.dto.AlunoDTO;
import br.com.lottus.edu.library.model.Aluno;
import br.com.lottus.edu.library.repository.AlunoRepository;
import br.com.lottus.edu.library.service.AlunoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;


@Tag(name = "Alunos", description = "Endpoint para o gerenciamento de alunos")
@RestController
@RequestMapping("/alunos")
public class AlunoController {

    @Autowired
    private final AlunoService alunoService;

    @Autowired
    private final AlunoRepository alunoRepository;

    public AlunoController(AlunoService alunoService, AlunoRepository alunoRepository) {
        this.alunoService = alunoService;
        this.alunoRepository = alunoRepository;
    }
    @Operation(summary = "Cadastra um novo aluno")
    @PostMapping("/cadastrar")
        public ResponseEntity<Aluno> adicionarAluno(@RequestBody AlunoDTO alunodto){

            System.out.println("Objeto alunoDTO" + alunodto.toString());
            Aluno newAluno = alunoService.adicionarAluno(alunodto);
            System.out.println("novo aluno a ser cadastrado" + newAluno.toString());

            return ResponseEntity.ok(newAluno);
        }

    @Operation(summary = "Remover aluno pelo numero da Matricula")

    @DeleteMapping("/remover/{matricula}")
    public ResponseEntity<String> removerAluno(@PathVariable String matricula){
        Optional<Aluno> aluno = alunoRepository.findByMatricula(matricula);

        if(aluno.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Aluno não encontrado");
        }

        boolean removido = alunoService.removerAluno(aluno.get());

        if(removido){
            return ResponseEntity.ok("Aluno removido com sucesso");
        }else{
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao remover o usuario");
        }
    }

    @Operation(summary = "Editar aluno pelo numero da Matricula")
    @PutMapping("/editar/{matricula}")
    public ResponseEntity<String> editarAluno(@PathVariable String matricula, @RequestBody AlunoDTO newAluno) {
        System.out.println("Matricula: " + matricula);
        Optional<Aluno> aluno = alunoRepository.findByMatricula(matricula);

        if(aluno.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Aluno não encontrado");
        }

        boolean editado = alunoService.editarAluno(matricula, newAluno);

        if(editado){
            return ResponseEntity.ok("Aluno editado com sucesso");
        }else{
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao editar usuario");
        }


    }

    @Operation(summary = "Obtem aluno pelo numero da Matricula")
    @GetMapping("/{matricula}")
    public ResponseEntity<Aluno> buscarPorMatricula(@PathVariable String matricula){
        return alunoService.buscarAlunoPorMatricula(matricula)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Obtem alunos de uma determinada turma")
    @GetMapping("/turma/{turmaId}")
    public ResponseEntity<Iterable<Aluno>> buscarPorTurma(@PathVariable Long turmaId){
        return ResponseEntity.ok(alunoService.listarAlunosPorTurma(turmaId));
    }

}
