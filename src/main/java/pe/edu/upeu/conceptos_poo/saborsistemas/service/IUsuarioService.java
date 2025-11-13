package pe.edu.upeu.conceptos_poo.saborsistemas.service;

import pe.edu.upeu.conceptos_poo.saborsistemas.modelos.Usuario;
import pe.edu.upeu.conceptos_poo.saborsistemas.modelos.VentCarrito;

import java.util.List;

public interface IUsuarioService extends ICrudGenericoService<Usuario,Long>{
    Usuario loginUsuario(String user, String clave);
}
