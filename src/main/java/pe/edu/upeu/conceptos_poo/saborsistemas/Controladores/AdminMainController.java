package pe.edu.upeu.conceptos_poo.saborsistemas.Controladores;


import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Controller;
import pe.edu.upeu.conceptos_poo.saborsistemas.components.StageManager;

import java.io.IOException;
import java.util.prefs.Preferences;

@Controller
public class AdminMainController {
    @Autowired
    private ConfigurableApplicationContext applicationContext; // Contexto de Spring

    @FXML
    private BorderPane bpAdminMain;
    @FXML private MenuBar menuBarAdmin;
    @FXML private TabPane tabPaneAdmin;
    @FXML private Menu menuEstilo;
    @FXML private Menu menuIdioma;

    // ComboBoxes para Estilo e Idioma
    private ComboBox<String> comboBoxEstilos;
    private ComboBox<String> comboBoxIdiomas;

    // Preferencias para guardar la selección del usuario
    Preferences userPrefs = Preferences.userRoot().node("pe/edu/upeu/saborsistemas/prefs");

    @FXML
    public void initialize() {
        configurarMenuEstilo();
        configurarMenuIdioma();
        abrirTabConFXML("/fxml/gestion_productos.fxml", "Gestionar Productos");
        abrirTabConFXML("/fxml/gestion_usuarios.fxml", "Gestionar Usuarios");
        abrirTabConFXML("/fxml/gestion_venta.fxml", "Gestionar Ventas");
        abrirTabConFXML("/fxml/gestion_productos.fxml", "Gestionar Productos");
    }





    @FXML
    private void abrirGestionUsuarios(ActionEvent event) {
        abrirTabConFXML("/fxml/gestion_usuarios.fxml", "Gestionar Usuarios");
    }

    // *** NUEVO: Método para abrir la pestaña de ventas ***
    @FXML
    private void abrirGestionVentas(ActionEvent event) {
        abrirTabConFXML("/fxml/gestion_venta.fxml", "Gestionar Ventas");
    }
    // *** FIN NUEVO ***


    // ... (resto de la clase)

    // --- Configuración de Menús ---

    private void configurarMenuEstilo() {
        comboBoxEstilos = new ComboBox<>(
                FXCollections.observableArrayList(
                        "Estilo por Defecto", // Asume que tienes un styles.css base
                        "Estilo Oscuro",     // Asume /css/estilo-oscuro.css
                        "Estilo Azul",       // Asume /css/estilo-azul.css
                        "Estilo Verde",      // Asume /css/estilo-verde.css
                        "Estilo Rosado"      // Asume /css/estilo-rosado.css
                )
        );
        comboBoxEstilos.setValue(userPrefs.get("appEstilo", "Estilo por Defecto")); // Cargar preferencia
        comboBoxEstilos.setOnAction(e -> cambiarEstilo());

        CustomMenuItem customItemEstilo = new CustomMenuItem(comboBoxEstilos);
        customItemEstilo.setHideOnClick(false);
        menuEstilo.getItems().add(customItemEstilo);
    }

    private void configurarMenuIdioma() {
        comboBoxIdiomas = new ComboBox<>(
                FXCollections.observableArrayList(
                        "Español", // es
                        "Inglés"   // en
                        // Añadir más si tienes archivos properties
                )
        );
        // Mapeo simple, puedes hacerlo más robusto
        String langCode = userPrefs.get("appIdioma", "es");
        comboBoxIdiomas.setValue(langCode.equals("es") ? "Español" : "Inglés");

        comboBoxIdiomas.setOnAction(e -> cambiarIdioma());

        CustomMenuItem customItemIdioma = new CustomMenuItem(comboBoxIdiomas);
        customItemIdioma.setHideOnClick(false);
        menuIdioma.getItems().add(customItemIdioma);
    }

    // --- Acciones de Menú ---

    @FXML
    private void abrirGestionProductos(ActionEvent event) {
        abrirTabConFXML("/fxml/gestion_productos.fxml", "Gestionar Productos");
    }



    @FXML
    private void cerrarSesion(ActionEvent event) {
        // Lógica para cerrar sesión: limpiar SessionManager, volver a login
        System.out.println("Cerrando sesión...");

        try {
            Stage stage = StageManager.getPrimaryStage(); // Obtener stage principal
            if (stage == null) { // Fallback por si StageManager falla
                stage = (Stage) bpAdminMain.getScene().getWindow();
            }

            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
            fxmlLoader.setControllerFactory(applicationContext::getBean);
            Parent loginRoot = fxmlLoader.load();
            Scene scene = new Scene(loginRoot);
            stage.setScene(scene);
            stage.setTitle("SaborSistemas - Login"); // Restablecer título
            // stage.centerOnScreen(); // Opcional: centrar ventana
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error al volver a la pantalla de login.");
        }
    }

    @FXML
    private void salirAplicacion(ActionEvent event) {
        Platform.exit();
        System.exit(0);
    }

    // --- Lógica Auxiliar ---

    private void cambiarEstilo() {
        String estiloSeleccionado = comboBoxEstilos.getValue();
        if (estiloSeleccionado == null) return;

        Scene escena = bpAdminMain.getScene();
        if (escena == null) return; // La escena podría no estar lista aún

        escena.getStylesheets().clear(); // Limpia estilos anteriores
        String cssPath = null;

        switch (estiloSeleccionado) {
            case "Estilo Oscuro":
                cssPath = "/css/estilo-oscuro.css"; // Asegúrate que estos archivos existan
                break;
            case "Estilo Azul":
                cssPath = "/css/estilo-azul.css";
                break;
            case "Estilo Verde":
                cssPath = "/css/estilo-verde.css";
                break;
            case "Estilo Rosado":
                cssPath = "/css/estilo-rosado.css";
                break;
            default:
                cssPath = "/css/styles.css"; // Asume un estilo base
                break;
        }

        if (cssPath != null && getClass().getResource(cssPath) != null) {
            escena.getStylesheets().add(getClass().getResource(cssPath).toExternalForm());
            userPrefs.put("appEstilo", estiloSeleccionado); // Guardar preferencia
            System.out.println("Estilo cambiado a: " + estiloSeleccionado);
        } else if (cssPath != null) {
            System.err.println("No se encontró el archivo CSS: " + cssPath);
        } else {
            System.out.println("Restableciendo a estilo por defecto.");
            userPrefs.put("appEstilo", "Estilo por Defecto");
        }
    }


    private void cambiarIdioma() {
        String idiomaSeleccionado = comboBoxIdiomas.getValue();
        if (idiomaSeleccionado == null) return;

        String langCode = "es"; // Default
        switch (idiomaSeleccionado) {
            case "Español": langCode = "es"; break;
            case "Inglés": langCode = "en"; break;
            // Añadir más casos
        }

        userPrefs.put("appIdioma", langCode); // Guardar preferencia
        System.out.println("Idioma cambiado a: " + langCode);

        // --- Reiniciar UI para aplicar idioma ---
        // Esto es lo más complejo. Una forma simple pero drástica es recargar todo.
        // Una mejor solución (más avanzada) implicaría actualizar textos dinámicamente
        // usando ResourceBundle y bindings, o reiniciar solo la escena actual.

        // Ejemplo simple: Recargar la escena principal (requiere reiniciar el Stage)
        mostrarAlertaReinicioIdioma();

        // Opcional: Si tienes una forma de actualizar los textos sin reiniciar:
        // actualizarTextosUI(langCode); // Necesitarías implementar este método
    }

    private void mostrarAlertaReinicioIdioma() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Cambio de Idioma");
        alert.setHeaderText("Reinicia la aplicación");
        alert.setContentText("El cambio de idioma requiere reiniciar la aplicación para tener efecto completo.");
        alert.showAndWait();
        // Aquí podrías cerrar y relanzar la app, pero es complejo.
        // Por ahora, solo informamos al usuario.
    }

    /**
     * Carga un archivo FXML en una nueva pestaña dentro del TabPane principal.
     * Si ya existe una pestaña con el mismo título, la selecciona.
     * @param fxmlPath Ruta al archivo FXML (ej. "/fxml/admin_gestionar_productos.fxml")
     * @param tituloTab Título que tendrá la nueva pestaña.
     */
    private void abrirTabConFXML(String fxmlPath, String tituloTab) {
        // Buscar si ya existe una pestaña con ese título
        for (Tab tab : tabPaneAdmin.getTabs()) {
            if (tab.getText().equals(tituloTab)) {
                tabPaneAdmin.getSelectionModel().select(tab); // Seleccionar la existente
                return; // Salir, no crear una nueva
            }
        }

        // Si no existe, crear la nueva pestaña
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            loader.setControllerFactory(applicationContext::getBean); // IMPORTANTE para Spring
            Parent root = loader.load();

            Tab nuevaPestana = new Tab(tituloTab);
            nuevaPestana.setContent(root); // Añadir el contenido cargado

            tabPaneAdmin.getTabs().add(nuevaPestana); // Añadir la nueva pestaña
            tabPaneAdmin.getSelectionModel().select(nuevaPestana); // Seleccionarla

        } catch (IOException e) {
            e.printStackTrace();
            // Mostrar un Alert al usuario
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error al Cargar Módulo");
            alert.setHeaderText("No se pudo cargar la vista: " + tituloTab);
            alert.setContentText("Detalle: " + e.getMessage());
            alert.showAndWait();
        }
    }
}
