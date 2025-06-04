package br.com.lottus.edu.library.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "password_reset_token")
public class PasswordResetToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "fk_usuario", unique = false)
    private Usuario usuario;

    @Column(unique = true)
    private UUID token;

    @Column(unique = true)
    private UUID publicToken;

    @Column(nullable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant expiresAt;

    private Boolean used = false;

    @PrePersist
    public void prePersist(){
        createdAt = Instant.now();
        expiresAt = createdAt.plus(15, ChronoUnit.MINUTES);

        if(publicToken == null){
            publicToken = UUID.randomUUID();
        }
        if(token == null){
            token = UUID.randomUUID();
        }
    }

}
