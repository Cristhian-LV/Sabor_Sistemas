package pe.edu.upeu.conceptos_poo.saborsistemas.Controladores;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import pe.edu.upeu.conceptos_poo.saborsistemas.modelos.Producto; // Importar el modelo real
import pe.edu.upeu.conceptos_poo.saborsistemas.service.ProductoIService;

import java.util.List;

@Controller // Añadir si no estaba
public class SelectorProductosController {

    @FXML private TableView<Producto> tablaProductos; // Ahora usa el modelo Producto directamente
    @FXML private TableColumn<Producto, String> colCodigo;
    @FXML private TableColumn<Producto, String> colNombre;
    @FXML private TableColumn<Producto, Double> colPrecio;
    @FXML private TableColumn<Producto, Long> colStock; // El stock en Producto es Long

    // Inyectar el servicio de productos
    @Autowired
    private ProductoIService productoService;

    // Lista observable para la tabla
    private final ObservableList<Producto> lista = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Configurar las columnas para usar el modelo Producto
        colCodigo.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().getId_producto()))); // Usar ID
        colNombre.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNombre()));
        colPrecio.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().getPrecioU())); // Usar PrecioU
        colStock.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().getStok())); // Usar stok (Long)

        // Cargar productos desde el servicio
        cargarProductos();

        tablaProductos.setItems(lista); // Asignar la lista a la tabla
    }

    private void cargarProductos() {
        try {
            List<Producto> productosDB = productoService.findAllProductos();
            lista.setAll(productosDB); // Carga la lista observable
        } catch (Exception e) {
            e.printStackTrace();
            // Mostrar alerta de error al usuario
            Alert alert = new Alert(Alert.AlertType.ERROR, "No se pudo cargar la lista de productos: " + e.getMessage());
            alert.showAndWait();
        }
    }


    @FXML
    private void seleccionarProducto() {
        Producto sel = tablaProductos.getSelectionModel().getSelectedItem(); // Obtiene el Producto seleccionado
        if (sel == null) {
            new Alert(Alert.AlertType.WARNING, "Selecciona un producto.").showAndWait();
            return;
        }

        // Crear un ProductoItem temporal para pasar solo los datos necesarios
        // (Asegúrate que la clase ProductoItem exista en ProductoSeleccionadoHolder o aquí)
        ProductoSeleccionadoHolder.ProductoItem itemSeleccionado = new ProductoSeleccionadoHolder.ProductoItem(
                String.valueOf(sel.getId_producto()),
                sel.getNombre(),
                sel.getPrecioU() != null ? sel.getPrecioU() : 0.0,
                sel.getStok() != null ? sel.getStok().intValue() : 0 // Convertir Long a int para el holder
        );

        // Guardamos el ProductoItem en el Holder
        ProductoSeleccionadoHolder.setProducto(itemSeleccionado);

        // Cerrar la ventana del selector
        cerrarVentana();
    }


    @FXML
    private void cerrarVentana() {
        Stage stage = (Stage) tablaProductos.getScene().getWindow();
        if (stage != null) {
            stage.close();
        }
    }

    // --- Clase interna ProductoItem (DEBE EXISTIR EN ProductoSeleccionadoHolder o aquí) ---
    // Si la moviste a ProductoSeleccionadoHolder, elimina esta definición de aquí.
    // Si la dejas aquí, asegúrate de que ProductoSeleccionadoHolder la use correctamente.
      /*
      public static class ProductoItem {
          // ... (definición como en la respuesta anterior)
      }
      */
}