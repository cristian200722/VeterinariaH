package com.veterinariah.auth.exception;

public class CodigoRecuperacionInvalidoException extends RuntimeException {
    public CodigoRecuperacionInvalidoException() {
        super("El código no es válido o ya venció. Solicita uno nuevo.");
    }
}
