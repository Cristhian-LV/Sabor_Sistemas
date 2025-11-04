package pe.edu.upeu.conceptos_poo.saborsistemas.modelos;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder //Colocar los valores sin instanciar
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table (name ="ss_usuario")

public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)  //Generar id sucesivamente
    @Column(name = "id_usuario")
    private Long idUsuario;
    @Column(name = "nombre_user", nullable = false, unique = true) // unique es para que solo aya un usuario con ese nombre
    private String nombre_Usuario;
    @Column(name = "Clave", nullable = false)
    private String clave;
    @Column(name ="rol", nullable = false)
    private String rol;
    @JoinColumn(name = "id_perfil", referencedColumnName = "id_perfil") //En Usuario, la columna que guarda la relacion se llama id_perfil, se relaciona con el id_perfil de Perfil
    @ManyToOne (optional = false) //Muchos a uno, muchos usuarios pueden ser solo un rol, no pueden tener mas roles, optional false significa que la columna no puede star vacia
    private Perfil idPerfil;

}
