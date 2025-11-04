package pe.edu.upeu.conceptos_poo.saborsistemas.modelos;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pe.edu.upeu.conceptos_poo.saborsistemas.enums.TipoDocumento;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Data
@Table (name = "ss_Cliente")
public class Cliente {
    @Id
    @Column(name = "dni_ruc", nullable = false)
    private String dniruc;
    @Column(name = "nombres", nullable = false)
    private String nombres;
    @Column(name = "Tipo_Documento", nullable = false)
    @Enumerated(EnumType.STRING)
    private TipoDocumento tipoDocumento;

}
