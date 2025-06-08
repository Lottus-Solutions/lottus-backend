package br.com.lottus.edu.library.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CategoriaNaoEncontradaException.class)
    public ResponseEntity<String> handleCategoriaNaoEncontrada(CategoriaNaoEncontradaException e)   {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

    @ExceptionHandler(LivroNaoEncontradoException.class)
    public ResponseEntity<String> handleLivroNaoEncontrado(LivroNaoEncontradoException e)  {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

    @ExceptionHandler(NenhumLivroEncontradoException.class)
    public ResponseEntity<String> handleNenhumLivroEncontradoException(NenhumLivroEncontradoException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

    @ExceptionHandler(EmprestimoNaoEncontradoException.class)
    public ResponseEntity<String> handleEmprestimoNaoEncontrado(EmprestimoNaoEncontradoException e){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

    @ExceptionHandler(MultiClassNotFundException.class)
    public ResponseEntity<String> handleMultiClassNaoEncontada(MultiClassNotFundException e){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

    @ExceptionHandler(EmprestimoAtivoException.class)
    public ResponseEntity<String> handleEmprestimoAtivo(EmprestimoAtivoException e){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }

    @ExceptionHandler(LivroIndisponivelException.class)
    public ResponseEntity<String> handleLivroInvalidoException(LivroIndisponivelException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }

    @ExceptionHandler(TurmaNaoEncontradaException.class)
    public ResponseEntity<String> handleTurmaNaoEncontrada(TurmaNaoEncontradaException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

    @ExceptionHandler(TurmaJaCadastradaException.class)
    public ResponseEntity<String> handleTurmaJaCadastrada(TurmaJaCadastradaException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
    }

    @ExceptionHandler(StatusInvalidoException.class)
    public ResponseEntity<String> handleStatusInvalido(StatusInvalidoException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }

    @ExceptionHandler(EmailJaCadastradoException.class)
    public ResponseEntity<String> handleEmailJaCadastrado(EmailJaCadastradoException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }

    @ExceptionHandler(TurmaNaoExisteException.class)
    public ResponseEntity<String> handleTurmaNaoExiste(TurmaNaoExisteException e){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }

    @ExceptionHandler(AlunoNaoEncontradoException.class)
    public ResponseEntity<String> handleAlunoNaoEncontrado(AlunoNaoEncontradoException e ){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

    @ExceptionHandler(CategoriaJaExistenteException.class)
    public ResponseEntity<String> handleCategoriaJaExistente(CategoriaJaExistenteException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
    }

    @ExceptionHandler(CategoriaInvalidaException.class)
    public ResponseEntity<String> handleCategoriaInvalida(CategoriaInvalidaException e){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }

    @ExceptionHandler(LivroComEmprestimosAtivosException.class)
    public ResponseEntity<String> handleLivroComEmprestimosAtivos(LivroComEmprestimosAtivosException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
    }

}
