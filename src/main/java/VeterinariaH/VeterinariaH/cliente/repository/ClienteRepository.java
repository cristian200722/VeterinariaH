package VeterinariaH.VeterinariaH.cliente.repository;
import VeterinariaH.VeterinariaH.cliente.entity.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
public interface ClienteRepository extends JpaRepository<Cliente, Long> {
      boolean existsByEmail(String email);
}
