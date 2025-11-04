package pe.edu.upeu.conceptos_poo.saborsistemas.service;

import pe.edu.upeu.conceptos_poo.saborsistemas.modelos.Usuario;

public interface UsuarioService extends CRUD_GenericoSefvice_Interface<Usuario, Long>{
    Usuario loginUsuario(String user, String clave);
    // --- NUEVO MÉTODO ---
    /**
     * Verifica si un nombre de usuario ya existe en la base de datos.
     * @param nombreUsuario El nombre de usuario a verificar.
     * @return true si el usuario existe, false en caso contrario.
     */
    boolean existeUsuario(String nombreUsuario);
}
