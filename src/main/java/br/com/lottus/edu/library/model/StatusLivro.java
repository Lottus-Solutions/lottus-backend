package br.com.lottus.edu.library.model;

import br.com.lottus.edu.library.exception.StatusInvalidoException;

public enum StatusLivro {
    DISPONIVEL,
    RESERVADO;

    public static StatusLivro fromString(String status) {
        for (StatusLivro s : StatusLivro.values()) {
            if (s.name().equalsIgnoreCase(status)) {
                return s;
            }
        }
        throw new StatusInvalidoException(status);
    }
}
