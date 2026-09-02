package VeterinariaH.VeterinariaH.shared.exception;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;

public class RecursoNoEncontradoException extends RuntimeException {

    public RecursoNoEncontradoException(String mensaje) {
        super(mensaje);
    }
    @ExceptionHandler(RecursoNoEncontradoException.class)
public ResponseEntity<Map<String, String>> manejarRecursoNoEncontrado(
        RecursoNoEncontradoException excepcion
) {
    return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(Map.of("error", excepcion.getMessage()));
}
}