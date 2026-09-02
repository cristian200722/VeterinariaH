package VeterinariaH.VeterinariaH.service;
import org.springframework.stereotype.Service;
import VeterinariaH.VeterinariaH.cliente.repository.ClienteRepository;
import VeterinariaH.VeterinariaH.shared.exception.RecursoNoEncontradoException;
import VeterinariaH.VeterinariaH.cliente.entity.Cliente;
import VeterinariaH.VeterinariaH.cliente.dto.ClienteCrearDto;
import VeterinariaH.VeterinariaH.cliente.dto.ClienteRespuestaDto;
import java.util.List;
import lombok.RequiredArgsConstructor;
@Service
@RequiredArgsConstructor
public class ClienteService {
         private final ClienteRepository clienteRepository;
        
        public ClienteRespuestaDto crear(ClienteCrearDto dto){
            String emailNormalizado = dto.getEmail().toLowerCase();
            if (clienteRepository.existsByEmail(emailNormalizado)){
                throw new IllegalArgumentException("El email ya está registrado");
            }
            Cliente cliente = new Cliente();
            cliente.setNombre(dto.getNombre());
            cliente.setApellido(dto.getApellido());
            cliente.setTelefono(dto.getTelefono());
            cliente.setEmail(emailNormalizado);
            cliente.setDireccion(dto.getDireccion());
            
            Cliente clienteGuardado = clienteRepository.save(cliente);
            return convertirARespuestaDto(clienteGuardado);
        }


        public List<ClienteRespuestaDto> listarClientes(){
            List<Cliente> clientes = clienteRepository.findAll();
            return clientes.stream()
                    .map(this::convertirARespuestaDto)
                    .toList();
        }
         public ClienteRespuestaDto obtenerPorId(Long id) {
    Cliente cliente = clienteRepository.findById(id)
            .orElseThrow(() -> new RecursoNoEncontradoException(
                    "No existe un cliente con el id " + id
            ));

    return convertirARespuestaDto(cliente);
}
public ClienteRespuestaDto actualizar(Long id, ClienteCrearDto dto) {
    Cliente cliente = clienteRepository.findById(id)
            .orElseThrow(() -> new RecursoNoEncontradoException(
                    "No existe un cliente con el id " + id
            ));

    String emailNormalizado = dto.getEmail().trim().toLowerCase();

    boolean emailCambio = !cliente.getEmail().equals(emailNormalizado);

    if (emailCambio && clienteRepository.existsByEmail(emailNormalizado)) {
        throw new IllegalArgumentException("El email ya está registrado");
    }

    cliente.setNombre(dto.getNombre());
    cliente.setApellido(dto.getApellido());
    cliente.setTelefono(dto.getTelefono());
    cliente.setEmail(emailNormalizado);
    cliente.setDireccion(dto.getDireccion());

    Cliente clienteActualizado = clienteRepository.save(cliente);

    return convertirARespuestaDto(clienteActualizado);
}

   private ClienteRespuestaDto convertirARespuestaDto(Cliente cliente){
        return new ClienteRespuestaDto(
                cliente.getId(),
                cliente.getNombre(),
                cliente.getApellido(),
                cliente.getTelefono(),
                cliente.getEmail(),
                cliente.getDireccion()
        );
    }
        public void eliminar(Long id) {
    Cliente cliente = clienteRepository.findById(id)
            .orElseThrow(() -> new RecursoNoEncontradoException(
                    "No existe un cliente con el id " + id
            ));

    clienteRepository.delete(cliente);
}
}
