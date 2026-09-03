package com.veterinariah.shared.exception;

public class RecursoYaExistenteException extends RuntimeException {

    public RecursoYaExistenteException(String mensaje) {
        super(mensaje);
    }
}
