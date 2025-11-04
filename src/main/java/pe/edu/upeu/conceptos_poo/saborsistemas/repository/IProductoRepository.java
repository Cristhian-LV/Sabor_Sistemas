package pe.edu.upeu.conceptos_poo.saborsistemas.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pe.edu.upeu.conceptos_poo.saborsistemas.modelos.Producto;

import java.util.List;

public interface IProductoRepository extends JpaRepository<Producto, Long> {
    // Aquí puedes agregar métodos personalizados si necesitas realizar consultas específicas
    @Query(value = "SELECT p.* FROM upeu_producto p WHERE p.nombre like :filter", nativeQuery = true)
    List<Producto> listAutoCompletProducto(@Param("filter") String filter);

    @Query(value = "SELECT p.* FROM upeu_producto p WHERE p.id_marca=:filter", nativeQuery = true)
    List<Producto> listProductoMarca(@Param("filter") Integer filter);
}
