package pe.edu.upeu.conceptos_poo.saborsistemas.modelos;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "ss_U.Medida")
public class UnidadMedida {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_unidad",nullable = false)
    private Long id_unidad;
    @Column(name = "nombre_medida", nullable = false, length = 60)
    private String nombre_Medida;
}
