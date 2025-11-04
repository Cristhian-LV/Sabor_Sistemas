package pe.edu.upeu.conceptos_poo.saborsistemas.modelos;

import com.fasterxml.jackson.annotation.JsonIgnore; // Para evitar recursión infinita en JSON
import jakarta.persistence.*;
import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "ss_detalle_venta") // Nueva tabla
public class DetalleVenta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_detalle_venta")
    private Long idDetalleVenta;

    @ManyToOne(optional = false, fetch = FetchType.LAZY) // El detalle DEBE pertenecer a una venta
    @JoinColumn(name = "id_venta", referencedColumnName = "id_venta")
    @JsonIgnore // Ignora la propiedad 'venta' al serializar DetalleVenta a JSON
    private Venta venta;

    @ManyToOne(optional = false) // El detalle DEBE tener un producto
    @JoinColumn(name = "id_producto", referencedColumnName = "id_producto")
    private Producto producto; // Enlazamos al producto real

    @Column(name = "precio_unitario", nullable = false)
    private Double precioUnitario; // Precio al momento de la venta

    @Column(name = "cantidad", nullable = false)
    private Integer cantidad; // Cantidad vendida (usamos Integer)

    @Column(name = "subtotal", nullable = false)
    private Double subtotal; // cantidad * precioUnitario
}