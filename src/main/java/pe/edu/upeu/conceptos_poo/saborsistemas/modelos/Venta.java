package pe.edu.upeu.conceptos_poo.saborsistemas.modelos;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "ss_venta") // Nueva tabla
public class Venta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_venta")
    private Long idVenta;

    // Opcional: Relación con Cliente si lo buscas/guardas por DNI
    // @ManyToOne
    // @JoinColumn(name = "dni_cliente", referencedColumnName = "dni_ruc")
    // private Cliente cliente;

    // Guardamos los datos directamente
    @Column(name = "dni_cliente", length=20) // Añadido DNI
    private String dniCliente;
    @Column(name = "nombre_cliente")
    private String nombreCliente;
    @Column(name = "apellido_cliente")
    private String apellidoCliente;

    @Column(name = "metodo_pago", length = 50)
    private String metodoPago;

    @Column(name = "numero_tarjeta", length = 20, nullable = true) // Nullable si es efectivo
    private String numeroTarjeta;


    @Column(name = "monto_total", nullable = false)
    private Double montoTotal;

    @ManyToOne(optional = false) // La venta debe tener un usuario
    @JoinColumn(name = "id_usuario", referencedColumnName = "id_usuario")
    private Usuario usuario; // Usuario que realizó la venta

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss") // Formato de fecha
    @Column(name = "fecha_venta", nullable = false)
    private LocalDateTime fechaVenta;


    // Relación con el detalle: Una venta tiene muchos detalles
    @OneToMany(mappedBy = "venta", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER) // EAGER para cargar detalles al cargar venta
    private List<DetalleVenta> detalleVenta;
}
