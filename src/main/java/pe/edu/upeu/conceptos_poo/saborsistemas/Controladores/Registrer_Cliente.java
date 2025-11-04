package pe.edu.upeu.conceptos_poo.saborsistemas.Controladores;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import pe.edu.upeu.conceptos_poo.saborsistemas.modelos.Perfil;
import pe.edu.upeu.conceptos_poo.saborsistemas.modelos.Usuario;
import pe.edu.upeu.conceptos_poo.saborsistemas.service.PerfilService;
import pe.edu.upeu.conceptos_poo.saborsistemas.service.UsuarioService;

import java.io.IOException;
import java.util.regex.Pattern;

@Controller
public class Registrer_Cliente {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Button registerButton;
    @FXML private Button backButton;
    @FXML private Label messageLabel;
    @FXML private Button goToLoginButton;

    @Autowired private UsuarioService usuarioService;
    @Autowired private ConfigurableApplicationContext applicationContext;
    @Autowired private PerfilService perfilService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    //Restriccion de correo
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$"); // Más robusto

    //Restriccion de contraseña
    private static final Pattern PASSWORD_PATTERN = Pattern.compile(
            "^(?=.*[0-9])(?=.*[A-Z])(?=.*[!@#\\$%\\^&\\*\\(\\)_\\+\\-=\\{\\}\\[\\]\\|\\\\:;\"'<>,\\.?/~`])(?=\\S+$).{6,}$"
    );


    private static final String ROL_CLIENTE = "Cliente";
    // ¡ASEGÚRATE DE QUE ESTE ID SEA EL CORRECTO PARA EL PERFIL 'Cliente' EN TU DB!
    private static final Long ID_PERFIL_CLIENTE = 1L;

    @FXML
    public void initialize() {
        registerButton.setOnAction(e -> Registrarce());
        backButton.setOnAction(e -> VolverInicio());
        goToLoginButton.setOnAction(e -> IngresarLoguin());
    }


    private void Registrarce() {
        String email = usernameField.getText().trim();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        messageLabel.setText("");
        messageLabel.setStyle("");

        if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            messageLabel.setText("Todos los campos son obligatorios.");
            messageLabel.setStyle("-fx-text-fill: red;");
            return;
        }
        // Validacion de correo
        if (!EMAIL_PATTERN.matcher(email).matches()) {

            messageLabel.setText("El formato del nombre de usuario debe ser un correo válido (ej. usuario@dominio.com).");
            messageLabel.setStyle("-fx-text-fill: red;");
            return;
        }


        if (!password.equals(confirmPassword)) {
            messageLabel.setText("Las contraseñas no coinciden.");
            messageLabel.setStyle("-fx-text-fill: red;");
            return;
        }

        // Validar Contraseña
        if (!PASSWORD_PATTERN.matcher(password).matches()) {
            messageLabel.setText("La contraseña debe tener al menos 6 caracteres, una mayúscula, un número y un carácter especial (!@#$%, etc.).");
            messageLabel.setStyle("-fx-text-fill: red;");
            // Puedes ser más específico si quieres:
             if (password.length() < 6) { messageLabel.setText("Contraseña muy corta (mínimo 6).");}
             else if (!password.matches(".*[A-Z].*")) { messageLabel.setText("Falta mayúscula en la contraseña");}
             else if (!password.matches(".*[0-9].*")) { messageLabel.setText("Falta número en la contraseña.");}
             else if (!password.matches(".*[!@#$%^&*()].*")) { messageLabel.setText("Falta carácter especial.");}
            return;
        }

        try {
            // Verifica si el usuario exixte o no con el mismo nobre
            if (usuarioService.existeUsuario(email)) {
                messageLabel.setText("El nombre de usuario '" + email + "' ya está en uso. Por favor, elija otro.");
                return; // Detiene el registro si el usuario ya existe
            }


            Perfil perfilCliente = perfilService.findById(ID_PERFIL_CLIENTE);
            if (perfilCliente == null) {
                messageLabel.setText("Error interno: No se encontró el perfil de cliente configurado.");
                System.err.println("CRÍTICO: No se encontró el Perfil con ID " + ID_PERFIL_CLIENTE + " en la base de datos.");
                return;
            }

            Usuario nuevoUsuario = new Usuario();
            nuevoUsuario.setNombre_Usuario(email);
            nuevoUsuario.setClave(passwordEncoder.encode(password));
            nuevoUsuario.setRol(ROL_CLIENTE);
            nuevoUsuario.setIdPerfil(perfilCliente);

            usuarioService.save(nuevoUsuario);

            //Para el inicio de sesion
            messageLabel.setText("¡Registro exitoso! Ahora puedes iniciar sesión.");
            messageLabel.setStyle("-fx-text-fill: lightgreen;");

            // Ocultar campos y botón de registro
            usernameField.setVisible(false);
            passwordField.setVisible(false);
            confirmPasswordField.setVisible(false);
            registerButton.setVisible(false);

            // Mostrar el botón "Iniciar Sesión"
            goToLoginButton.setVisible(true);

        } catch (Exception e) {
            messageLabel.setText("Error inesperado durante el registro. Inténtelo más tarde.");
            messageLabel.setStyle("-fx-text-fill: red;");
            e.printStackTrace();
        }

    }

    /**
     * Lógica interna para manejar el clic en el botón "Volver al Inicio".
     */
    private void VolverInicio() {
        try {
            Stage stage = (Stage) backButton.getScene().getWindow();

            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));

            fxmlLoader.setControllerFactory(applicationContext::getBean);

            Parent parent = fxmlLoader.load();

            Scene scene = new Scene(parent);

            stage.setScene(scene);

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error al volver a la pantalla principal /fxml/login.fxml");
        } catch (NullPointerException e) {
            e.printStackTrace();
            System.err.println("Error: No se pudo obtener la ventana desde el botón 'Volver'.");
        }
    }
    private void IngresarLoguin() {
        try {
            Stage stage = (Stage) goToLoginButton.getScene().getWindow();

            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));

            fxmlLoader.setControllerFactory(applicationContext::getBean);

            Parent parent = fxmlLoader.load();

            Scene scene = new Scene(parent);

            stage.setScene(scene);

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error al cargar la pantalla de login /fxml/login.fxml");
        } catch (NullPointerException e) {
            e.printStackTrace();
            System.err.println("Error: No se pudo obtener la ventana desde el botón 'Iniciar Sesión'.");
        }
    }

}
