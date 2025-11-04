package pe.edu.upeu.conceptos_poo.saborsistemas.service.imp;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pe.edu.upeu.conceptos_poo.saborsistemas.modelos.Usuario;
import pe.edu.upeu.conceptos_poo.saborsistemas.repository.ICrudGenericoRepository;
import pe.edu.upeu.conceptos_poo.saborsistemas.repository.IUsuarioRepository;
import pe.edu.upeu.conceptos_poo.saborsistemas.service.UsuarioService;

@Service
@RequiredArgsConstructor
public class UsuarioImp extends CRUD_GenericoServiceImp<Usuario, Long> implements UsuarioService {

    private final IUsuarioRepository UsuaarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Override
    protected ICrudGenericoRepository<Usuario, Long> getRepository(){
        return UsuaarioRepository;
    }
    @Override
    public Usuario loginUsuario(String user, String clave) {
        // 1. Buscar al usuario solo por su nombre de usuario
        Usuario usuario = UsuaarioRepository.findByNombre_Usuario(user);

        // 2. Verificar si el usuario existe Y si la contraseña coincide (usando el encriptador)
        if (usuario != null && passwordEncoder.matches(clave, usuario.getClave())) {
            // Si la contraseña cruda (clave) coincide con la encriptada (usuario.getClave()), es exitoso
            return usuario;
        }

        // 3. Si no existe o la contraseña no coincide, retornar null
        return null;
    }

    @Override
    public boolean existeUsuario(String nombreUsuario) {
        return UsuaarioRepository.findByNombre_Usuario(nombreUsuario) != null;
    }

}
