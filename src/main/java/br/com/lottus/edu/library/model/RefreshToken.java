package br.com.lottus.edu.library.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "refresh_token")
@Getter
@Setter
@NoArgsConstructor
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_usuario", referencedColumnName = "id", nullable = false)
    private Usuario usuario;

    @Column(nullable = false, unique = true, length = 36) // O padrão do token UUID é 36 caracteres
    private String token;

    @Column(nullable = false)
    private Instant dataExpiracao;

    public RefreshToken(Usuario usuario, String token, Instant dataExpiracao){
        this.usuario = usuario;
        this.token = token;
        this.dataExpiracao = dataExpiracao;
    }


}
