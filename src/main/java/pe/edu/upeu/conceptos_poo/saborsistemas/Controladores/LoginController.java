package pe.edu.upeu.conceptos_poo.saborsistemas.Controladores;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Controller;
import pe.edu.upeu.conceptos_poo.saborsistemas.components.StageManager;
import pe.edu.upeu.conceptos_poo.saborsistemas.dto.SessionManager;
import pe.edu.upeu.conceptos_poo.saborsistemas.modelos.Usuario;
import pe.edu.upeu.conceptos_poo.saborsistemas.service.IUsuarioService;
import pe.edu.upeu.conceptos_poo.saborsistemas.service.UsuarioService;

import java.io.IOException;

@Controller
public class LoginController {
    // --- Componentes FXML ---
    // Estos fx:id deben coincidir con los de login.fxml
    @FXML
    private TextField emailField; // El FXML lo llama 'emailField', lo usaremos para 'nombre_user'

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button loginButton;

    @FXML
    private Button registrarButton;

    @FXML
    private Label errorMessageLabel;

    // --- Servicios de Spring ---
    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private ConfigurableApplicationContext applicationContext;
    @FXML
    public void initialize() {
        // Asignamos las acciones a los botones
        loginButton.setOnAction(e -> Entrar());
        registrarButton.setOnAction(e -> Volver());
    }

    @FXML
    private void Volver() {
        System.out.println("Botón 'Registrarse' presionado. Debes crear register.fxml");
        try {
            Stage stage = (Stage) registrarButton.getScene().getWindow();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/register.fxml"));
            fxmlLoader.setControllerFactory(applicationContext::getBean);
            Parent parent = fxmlLoader.load();
            Scene scene = new Scene(parent);
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
            System.err.println("Error al obtener la ventana para registro.");
        }
    }
    private void loadScene(String fxmlPath) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(fxmlPath));
            fxmlLoader.setControllerFactory(applicationContext::getBean);
            Parent parent = fxmlLoader.load();

            Stage stage = StageManager.getPrimaryStage();

            if (stage != null) {
                Scene scene = new Scene(parent);
                stage.setScene(scene); // Cambiamos la escena
            } else {
                System.err.println("Error: StageManager no tiene un Stage principal asignado.");
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error al cargar el FXML: " + fxmlPath);
        }
    }


    @FXML
    private void Entrar() {
        String username = emailField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            errorMessageLabel.setText("El usuario y la contraseña no pueden estar vacíos.");
            return;
        }

        try {
            Usuario usuario = usuarioService.loginUsuario(username, password);

            if (usuario != null) {
                errorMessageLabel.setText("¡Login exitoso! Bienvenido.");

                // *** NUEVO: Guardar en SessionManager ***
                SessionManager sm = SessionManager.getInstance();
                sm.setUserId(usuario.getIdUsuario());
                sm.setUserName(usuario.getNombre_Usuario());
                // Guarda el ROL directamente, o busca el nombre del Perfil si es necesario
                sm.setUserPerfil(usuario.getRol());
                // *** FIN NUEVO ***

                if ("Administrador".equalsIgnoreCase(usuario.getRol())) {
                    cargarEscenaAdmin("/fxml/admin_main.fxml", "Panel de Administrador");
                } else if ("Cliente".equalsIgnoreCase(usuario.getRol())) {
                    errorMessageLabel.setText("Login de Cliente exitoso. Interfaz en construcción.");
                    // cargarEscenaGenerica("/fxml/cliente_dashboard.fxml", "Panel de Cliente");
                } else if ("Vendedor".equalsIgnoreCase(usuario.getRol())) {
                    cargarEscenaAdmin("/fxml/main_venta.fxml", "Panel de Vendedor");
                } else {
                    // Rol desconocido, podrías mostrar un error o una interfaz genérica
                    errorMessageLabel.setText("Rol de usuario no reconocido: " + usuario.getRol());
                }


            } else {
                errorMessageLabel.setText("Usuario o contraseña incorrectos. Inténtelo de nuevo.");
            }
        } catch (Exception e) {
            errorMessageLabel.setText("Error al conectar con la base de datos: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ... (resto de la clase)
    private void cargarEscenaAdmin(String fxmlPath, String title) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(fxmlPath));
            fxmlLoader.setControllerFactory(applicationContext::getBean);
            Parent root = fxmlLoader.load();
            Stage stage = StageManager.getPrimaryStage();
            stage.getIcons().add(new Image(getClass().getResource("/img/logo_sabor_sistemas.png").toExternalForm()));
            if (stage == null) {
                stage = (Stage) loginButton.getScene().getWindow();
            }

            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle(title);
            stage.centerOnScreen(); // Centrar la ventana del admin
            // stage.setMaximized(true); // Opcional: maximizarla

        } catch (IOException e) {
            errorMessageLabel.setText("Error fatal: No se pudo cargar la interfaz principal.");
            e.printStackTrace();
        }
    }

}
