package pe.edu.upeu.conceptos_poo.saborsistemas.Controladores;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import pe.edu.upeu.conceptos_poo.saborsistemas.components.ColumnInfo;
import pe.edu.upeu.conceptos_poo.saborsistemas.components.TableViewHelper;
import pe.edu.upeu.conceptos_poo.saborsistemas.dto.ComboBoxOption;
import pe.edu.upeu.conceptos_poo.saborsistemas.modelos.Perfil;
import pe.edu.upeu.conceptos_poo.saborsistemas.modelos.Usuario;
import pe.edu.upeu.conceptos_poo.saborsistemas.service.PerfilService;
import pe.edu.upeu.conceptos_poo.saborsistemas.service.UsuarioService;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class Adm_UsuariosController {


    @FXML private TextField txtFiltroUsuarios;
    @FXML private TableView<Usuario> tableViewUsuarios;
    @FXML private TextField txtUsername;
    @FXML private PasswordField txtPassword;
    @FXML private ComboBox<ComboBoxOption> cbxPerfil;
    @FXML private Button btnGuardarUsuario;
    @FXML private Label lblMensajeUsuario;

    @Autowired
    private UsuarioService usuarioService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired private PerfilService perfilService;

    private ObservableList<Usuario> listaUsuariosObservable;
    private Long idUsuarioEditando = null;

    @FXML
    public void initialize() {

        cargarPerfiles();
        configurarTablaUsuarios();

        // Cargar datos iniciales
        listarUsuarios();

        // Listener para filtro
        txtFiltroUsuarios.textProperty().addListener((observable, oldValue, newValue) -> filtrarUsuarios(newValue));

        // Estado inicial del formulario
        cancelarEdicionUsuario();
    }

    private void cargarPerfiles() {
        try {
            List<Perfil> perfiles = perfilService.findAll();
            List<ComboBoxOption> opcionesPerfil = perfiles.stream()
                    .map(p -> new ComboBoxOption(String.valueOf(p.getId_perfil()), p.getCorreo())) // Asume que 'correo' es el nombre a mostrar
                    .collect(Collectors.toList());
            cbxPerfil.setItems(FXCollections.observableArrayList(opcionesPerfil));
        } catch (Exception e) {
            mostrarMensaje("Error al cargar perfiles: " + e.getMessage(), true);
            e.printStackTrace();
        }
    }

    private void configurarTablaUsuarios() {
        TableViewHelper<Usuario> helper = new TableViewHelper<>();
        LinkedHashMap<String, ColumnInfo> columnas = new LinkedHashMap<>();
        columnas.put("ID", new ColumnInfo("idUsuario", 60.0));
        columnas.put("Nombre Usuario (Correo)", new ColumnInfo("nombre_Usuario", 200.0));
        columnas.put("Rol", new ColumnInfo("rol", 100.0));
        columnas.put("Perfil (ID)", new ColumnInfo("idPerfil.id_perfil", 100.0)); // Mostrar ID del perfil

        helper.addColumnsInOrderWithSize(tableViewUsuarios, columnas, this::editarUsuario, this::eliminarUsuario);
        tableViewUsuarios.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS); // Ajustar columnas al ancho
    }

    private void listarUsuarios() {
        try {
            listaUsuariosObservable = FXCollections.observableArrayList(usuarioService.findAll());
            tableViewUsuarios.setItems(listaUsuariosObservable);
        } catch (Exception e) {
            mostrarMensaje("Error al listar usuarios: " + e.getMessage(), true);
            e.printStackTrace();
        }
    }

    private void filtrarUsuarios(String filtro) {
        if (filtro == null || filtro.isEmpty()) {
            tableViewUsuarios.setItems(listaUsuariosObservable);
        } else {
            String lowerCaseFilter = filtro.toLowerCase();
            List<Usuario> filtradosList = listaUsuariosObservable.stream()
                    .filter(u -> u.getNombre_Usuario().toLowerCase().contains(lowerCaseFilter) ||
                            (u.getRol() != null && u.getRol().toLowerCase().contains(lowerCaseFilter)))
                    .collect(Collectors.toList()); // <-- 1. Recolecta a una List normal
            ObservableList<Usuario> filtradosObservable = FXCollections.observableArrayList(filtradosList); // <-- 2. Convierte a ObservableList
            tableViewUsuarios.setItems(filtradosObservable);
        }
    }

    @FXML
    private void guardarUsuario() {
        // Validaciones básicas
        String username = txtUsername.getText().trim();
        String password = txtPassword.getText(); // No usamos trim() aquí
        ComboBoxOption perfilSeleccionado = cbxPerfil.getSelectionModel().getSelectedItem();

        if (username.isEmpty() || perfilSeleccionado == null) {
            mostrarMensaje("Nombre de usuario y rol son obligatorios.", true);
            return;
        }
        // Si es nuevo usuario O si se ingresó una nueva contraseña, esta es obligatoria
        if (idUsuarioEditando == null && password.isEmpty()) {
            mostrarMensaje("La contraseña es obligatoria para nuevos usuarios.", true);
            return;
        }
        // Validaciones de formato (correo, contraseña compleja) - ¡AÑADIR COMO EN RegisterClienteController!


        try {
            Usuario usuario = (idUsuarioEditando != null) ? usuarioService.findById(idUsuarioEditando) : new Usuario();
            if (usuario == null && idUsuarioEditando != null) {
                mostrarMensaje("Error: No se encontró el usuario a editar.", true);
                return;
            }
            if (usuario == null) usuario = new Usuario(); // Asegurarse que no sea null si es nuevo

            // Verificar si el username (correo) ya existe SI es un usuario nuevo O si se cambió el correo
            if (idUsuarioEditando == null || !usuario.getNombre_Usuario().equals(username)) {
                if (usuarioService.existeUsuario(username)) {
                    mostrarMensaje("El nombre de usuario (correo) ya está en uso.", true);
                    return;
                }
            }

            usuario.setNombre_Usuario(username);
            // Solo actualizar contraseña si se ingresó una nueva
            if (!password.isEmpty()) {
                // ***** MODIFICAR ESTA LÍNEA *****
                usuario.setClave(passwordEncoder.encode(password));
                // ********************************
            } else if (idUsuarioEditando == null) {
                mostrarMensaje("La contraseña es obligatoria para nuevos usuarios.", true);
                return;
            }
            Perfil perfil = perfilService.findById(Long.parseLong(perfilSeleccionado.getKey()));
            if (perfil == null) {
                mostrarMensaje("Error: Perfil seleccionado no válido.", true);
                return;
            }
            usuario.setIdPerfil(perfil);

            if (idUsuarioEditando == null) {
                usuarioService.save(usuario);
                mostrarMensaje("Usuario creado exitosamente.", false);
            } else {
                usuario.setIdUsuario(idUsuarioEditando); // Asegurarse de tener el ID para actualizar
                usuarioService.update(idUsuarioEditando, usuario); // O solo save si tu save maneja update
                mostrarMensaje("Usuario actualizado exitosamente.", false);
            }

            cancelarEdicionUsuario(); // Limpiar formulario y resetear ID
            listarUsuarios(); // Refrescar tabla

        } catch (Exception e) {
            mostrarMensaje("Error al guardar usuario: " + e.getMessage(), true);
            e.printStackTrace();
        }
    }

    private void editarUsuario(Usuario usuario) {
        idUsuarioEditando = usuario.getIdUsuario();
        txtUsername.setText(usuario.getNombre_Usuario());
        txtPassword.setPromptText("Dejar vacío para no cambiar"); // Indicar que no es obligatorio
        txtPassword.clear(); // Limpiar campo de contraseña para edición

        // Seleccionar el perfil correcto en el ComboBox
        String perfilIdStr = String.valueOf(usuario.getIdPerfil().getId_perfil());
        for (ComboBoxOption option : cbxPerfil.getItems()) {
            if (option.getKey().equals(perfilIdStr)) {
                cbxPerfil.setValue(option);
                break;
            }
        }

        lblMensajeUsuario.setText("Editando usuario ID: " + idUsuarioEditando);
        lblMensajeUsuario.setStyle("-fx-text-fill: blue;");
        btnGuardarUsuario.setText("Actualizar");
    }

    private void eliminarUsuario(Usuario usuario) {
        // Añadir confirmación antes de eliminar
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "¿Está seguro de eliminar al usuario '" + usuario.getNombre_Usuario() + "'?", ButtonType.YES, ButtonType.NO);
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                try {
                    usuarioService.delete(usuario.getIdUsuario());
                    mostrarMensaje("Usuario eliminado exitosamente.", false);
                    listarUsuarios(); // Refrescar la tabla
                } catch (Exception e) {
                    mostrarMensaje("Error al eliminar usuario: " + e.getMessage(), true);
                    e.printStackTrace();
                }
            }
        });
    }

    @FXML
    private void cancelarEdicionUsuario() {
        idUsuarioEditando = null;
        txtUsername.clear();
        txtPassword.clear();
        txtPassword.setPromptText("Contraseña"); // Restablecer prompt
        cbxPerfil.getSelectionModel().clearSelection();
        lblMensajeUsuario.setText("");
        btnGuardarUsuario.setText("Guardar");
        limpiarEstilosError();
    }

    private void mostrarMensaje(String mensaje, boolean esError) {
        lblMensajeUsuario.setText(mensaje);
        if (esError) {
            lblMensajeUsuario.setStyle("-fx-text-fill: red;");
        } else {
            lblMensajeUsuario.setStyle("-fx-text-fill: green;");
        }
    }

    private void limpiarEstilosError() {
        txtUsername.getStyleClass().remove("text-field-error");
        txtPassword.getStyleClass().remove("text-field-error");
        cbxPerfil.getStyleClass().remove("text-field-error");
    }
}
