package pe.edu.upeu.conceptos_poo.saborsistemas.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pe.edu.upeu.conceptos_poo.saborsistemas.modelos.VentCarrito;

import java.util.List;

public interface VentCarritoRepository extends ICrudGenericoRepository<VentCarrito,Long>{

    @Query(value = "SELECT c.* FROM upeu_vent_carrito c WHERE c.dniruc=:dniruc", nativeQuery = true)
    List<VentCarrito> listaCarritoCliente(@Param("dniruc") String dniruc);

    void deleteByDniruc(String dniruc);

}
