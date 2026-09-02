package VeterinariaH.VeterinariaH.cliente.controller;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import VeterinariaH.VeterinariaH.cliente.dto.ClienteCrearDto;
import VeterinariaH.VeterinariaH.cliente.dto.ClienteRespuestaDto;
import VeterinariaH.VeterinariaH.service.ClienteService;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/clientes")
public class ClienteController {
    
    private final ClienteService clienteService;
    @PostMapping
    public ResponseEntity<ClienteRespuestaDto> crear(
            @Valid @RequestBody ClienteCrearDto dto
    ) {
        ClienteRespuestaDto clienteCreado = clienteService.crear(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(clienteCreado);
    }
    @GetMapping
    public ResponseEntity<List<ClienteRespuestaDto>> listar() {
        return ResponseEntity.ok(clienteService.listarClientes());
    }
    @GetMapping("/{id}")
public ResponseEntity<ClienteRespuestaDto> obtenerPorId(
        @PathVariable Long id
) {
    return ResponseEntity.ok(clienteService.obtenerPorId(id));
}
@PutMapping("/{id}")
public ResponseEntity<ClienteRespuestaDto> actualizar(
        @PathVariable Long id,
        @Valid @RequestBody ClienteCrearDto dto
) {
    return ResponseEntity.ok(clienteService.actualizar(id, dto));
}
@DeleteMapping("/{id}")
public ResponseEntity<Void> eliminar(@PathVariable Long id) {
    clienteService.eliminar(id);

    return ResponseEntity.noContent().build();
}
    


}
