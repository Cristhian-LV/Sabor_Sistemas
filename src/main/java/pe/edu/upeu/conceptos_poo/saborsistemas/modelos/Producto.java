package pe.edu.upeu.conceptos_poo.saborsistemas.modelos;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "ss_Producto")
public class Producto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_producto",nullable = false)
    private Long id_producto;
    @Column(name = "Nombre",nullable = false)
    private String nombre;
    @Column (name = "Precio_U.", nullable=false)
    private Double PrecioU;
    @Column (name = "stock", nullable = false)
    private Long stok;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_categoria", referencedColumnName = "id_categoria")
    private Categoria categoria;

    @ManyToOne
    @JoinColumn(name = "id_unidad", referencedColumnName = "id_unidad", nullable = false)
    private UnidadMedida unidadMedida;


}
