package pe.edu.upeu.conceptos_poo.saborsistemas.repository;
import org.springframework.stereotype.Repository;
import pe.edu.upeu.conceptos_poo.saborsistemas.modelos.DetalleVenta;

@Repository
public interface IDetalleVentaRepository extends ICrudGenericoRepository<DetalleVenta, Long> {
}