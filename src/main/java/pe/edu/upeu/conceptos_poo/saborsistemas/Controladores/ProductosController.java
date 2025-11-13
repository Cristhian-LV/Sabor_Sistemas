package pe.edu.upeu.conceptos_poo.saborsistemas.Controladores;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import pe.edu.upeu.conceptos_poo.saborsistemas.components.ColumnInfo;
import pe.edu.upeu.conceptos_poo.saborsistemas.components.TableViewHelper;
import pe.edu.upeu.conceptos_poo.saborsistemas.dto.ComboBoxOption;
import pe.edu.upeu.conceptos_poo.saborsistemas.modelos.Categoria;
import pe.edu.upeu.conceptos_poo.saborsistemas.modelos.Producto;
import pe.edu.upeu.conceptos_poo.saborsistemas.modelos.UnidadMedida;
import pe.edu.upeu.conceptos_poo.saborsistemas.service.CategoriaIService;
import pe.edu.upeu.conceptos_poo.saborsistemas.service.ProductoIService;
import pe.edu.upeu.conceptos_poo.saborsistemas.service.UnidadMedidaService;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class ProductosController {

    @FXML private TextField txtFiltroProductos;
    @FXML private TableView<Producto> tableViewProductos;
    @FXML private TextField txtNombreProducto;
    @FXML private TextField txtPrecioUnitario;
    @FXML private TextField txtStockProducto;
    @FXML private ComboBox<ComboBoxOption> cbxCategoriaProducto;
    @FXML private ComboBox<ComboBoxOption> cbxUnidadMedidaProducto;
    @FXML private Button btnGuardarProducto;
    @FXML private Label lblMensajeProducto;

    @Autowired
    private ProductoIService productoService;
    @Autowired private CategoriaIService categoriaService;
    @Autowired private UnidadMedidaService unidadMedidaService;

    private ObservableList<Producto> listaProductosObservable;
    private Long idProductoEditando = null;

    @FXML
    public void initialize() {
        cargarCategorias();
        cargarUnidadesMedida();
        configurarTablaProductos();
        listarProductos();

        txtFiltroProductos.textProperty().addListener((observable, oldValue, newValue) -> filtrarProductos(newValue));
        cancelarEdicionProducto();
    }

    private void cargarCategorias() {
        try {
            // Usando el método que ya tenías en CategoriaImp
            List<ComboBoxOption> opciones = categoriaService.listarCombobox();
            cbxCategoriaProducto.setItems(FXCollections.observableArrayList(opciones));
        } catch (Exception e) {
            mostrarMensajeProducto("Error al cargar categorías: " + e.getMessage(), true);
            e.printStackTrace();
        }
    }

    private void cargarUnidadesMedida() {
        try {
            // Usando el método que ya tenías en UnidadMedidaImp
            List<ComboBoxOption> opciones = unidadMedidaService.listarCombobox();
            cbxUnidadMedidaProducto.setItems(FXCollections.observableArrayList(opciones));
        } catch (Exception e) {
            mostrarMensajeProducto("Error al cargar unidades de medida: " + e.getMessage(), true);
            e.printStackTrace();
        }
    }

    private void configurarTablaProductos() {
        TableViewHelper<Producto> helper = new TableViewHelper<>();
        LinkedHashMap<String, ColumnInfo> columnas = new LinkedHashMap<>();
        columnas.put("ID", new ColumnInfo("id_producto", 60.0));
        columnas.put("Nombre", new ColumnInfo("nombre", 200.0));
        columnas.put("Precio U.", new ColumnInfo("PrecioU", 100.0));
        columnas.put("Stock", new ColumnInfo("stok", 80.0));
        columnas.put("Categoría", new ColumnInfo("categoria.nombre", 150.0));
        columnas.put("Unidad M.", new ColumnInfo("unidadMedida.nombre_Medida", 150.0));

        helper.addColumnsInOrderWithSize(tableViewProductos, columnas, this::editarProducto, this::eliminarProducto);
        tableViewProductos.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
    }

    private void listarProductos() {
        try {

            listaProductosObservable = FXCollections.observableArrayList(productoService.findAllProductos());
            tableViewProductos.setItems(listaProductosObservable);

        } catch (Exception e) {

            mostrarMensajeProducto("Error al listar productos: " + e.getMessage(), true);
            e.printStackTrace();

        }
    }

    private void filtrarProductos(String filtro) {

        if (filtro == null || filtro.isEmpty()) {
            tableViewProductos.setItems(listaProductosObservable);
        }
        else {
            String lowerCaseFilter = filtro.toLowerCase();
            List<Producto> filtradosList = listaProductosObservable.stream()
                    .filter(p -> (p.getNombre() != null && p.getNombre().toLowerCase().contains(lowerCaseFilter)) ||
                            (p.getCategoria() != null && p.getCategoria().getNombre() != null && p.getCategoria().getNombre().toLowerCase().contains(lowerCaseFilter)) ||
                            (String.valueOf(p.getPrecioU()).contains(lowerCaseFilter)) )
                    .collect(Collectors.toList()); // <-- 1. Recolecta a una List normal
            ObservableList<Producto> filtradosObservable = FXCollections.observableArrayList(filtradosList); // <-- 2. Convierte a ObservableList
            tableViewProductos.setItems(filtradosObservable);
        }
    }

    @FXML
    private void guardarProducto() {

        String nombre = txtNombreProducto.getText().trim();
        String precioStr = txtPrecioUnitario.getText().trim();
        String stockStr = txtStockProducto.getText().trim();
        ComboBoxOption categoriaSeleccionada = cbxCategoriaProducto.getSelectionModel().getSelectedItem();
        ComboBoxOption unidadSeleccionada = cbxUnidadMedidaProducto.getSelectionModel().getSelectedItem();

        if (nombre.isEmpty() || precioStr.isEmpty() || stockStr.isEmpty() || categoriaSeleccionada == null || unidadSeleccionada == null) {
            mostrarMensajeProducto("Todos los campos son obligatorios.", true);
            return;
        }

        double precio;
        long stock;
        try {
            precio = Double.parseDouble(precioStr);
            if (precio < 0) throw new NumberFormatException("Precio no puede ser negativo");
        } catch (NumberFormatException e) {
            mostrarMensajeProducto("Ingrese un precio válido (número positivo).", true);
            return;
        }
        try {
            stock = Long.parseLong(stockStr);
            if (stock < 0) throw new NumberFormatException("Stock no puede ser negativo");
        } catch (NumberFormatException e) {
            mostrarMensajeProducto("Ingrese un stock válido (número entero positivo o cero).", true);
            return;
        }


        try {
            Producto producto = (idProductoEditando != null) ? productoService.findProductoById(idProductoEditando) : new Producto();
            if (producto == null && idProductoEditando != null) {
                mostrarMensajeProducto("Error: No se encontró el producto a editar.", true);
                return;
            }
            if (producto == null) producto = new Producto();

            producto.setNombre(nombre);
            producto.setPrecioU(precio);
            producto.setStok(stock);

            Categoria categoria = categoriaService.findById(Long.parseLong(categoriaSeleccionada.getKey()));
            UnidadMedida unidad = unidadMedidaService.findById(Long.parseLong(unidadSeleccionada.getKey()));

            if (categoria == null || unidad == null) {
                mostrarMensajeProducto("Error: Categoría o Unidad de Medida no válidas.", true);
                return;
            }
            producto.setCategoria(categoria);
            producto.setUnidadMedida(unidad);

            if (idProductoEditando == null) {
                productoService.saveProducto(producto);
                mostrarMensajeProducto("Producto creado exitosamente.", false);
            } else {
                producto.setId_producto(idProductoEditando);
                productoService.updateProducto(producto);
                mostrarMensajeProducto("Producto actualizado exitosamente.", false);
            }

            cancelarEdicionProducto();
            listarProductos();

        } catch (Exception e) {
            mostrarMensajeProducto("Error al guardar producto: " + e.getMessage(), true);
            e.printStackTrace();
        }
    }

    private void editarProducto(Producto producto) {
        idProductoEditando = producto.getId_producto();
        txtNombreProducto.setText(producto.getNombre());
        txtPrecioUnitario.setText(String.valueOf(producto.getPrecioU()));
        txtStockProducto.setText(String.valueOf(producto.getStok()));


        String catIdStr = String.valueOf(producto.getCategoria().getId_categoria());
        cbxCategoriaProducto.getItems().stream()
                .filter(opt -> opt.getKey().equals(catIdStr))
                .findFirst()
                .ifPresent(opt -> cbxCategoriaProducto.setValue(opt));


        String umIdStr = String.valueOf(producto.getUnidadMedida().getId_unidad());
        cbxUnidadMedidaProducto.getItems().stream()
                .filter(opt -> opt.getKey().equals(umIdStr))
                .findFirst()
                .ifPresent(opt -> cbxUnidadMedidaProducto.setValue(opt));


        lblMensajeProducto.setText("Editando producto ID: " + idProductoEditando);
        lblMensajeProducto.setStyle("-fx-text-fill: blue;");
        btnGuardarProducto.setText("Actualizar");
    }

    private void eliminarProducto(Producto producto) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "¿Está seguro de eliminar el producto '" + producto.getNombre() + "'?", ButtonType.YES, ButtonType.NO);
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                try {
                    productoService.deleteProductoById(producto.getId_producto());
                    mostrarMensajeProducto("Producto eliminado exitosamente.", false);
                    listarProductos();
                } catch (Exception e) {

                    mostrarMensajeProducto("Error al eliminar producto: " + e.getMessage(), true);
                    e.printStackTrace();
                }
            }
        });
    }

    @FXML
    private void cancelarEdicionProducto() {
        idProductoEditando = null;
        txtNombreProducto.clear();
        txtPrecioUnitario.clear();
        txtStockProducto.clear();
        cbxCategoriaProducto.getSelectionModel().clearSelection();
        cbxUnidadMedidaProducto.getSelectionModel().clearSelection();
        lblMensajeProducto.setText("");
        btnGuardarProducto.setText("Guardar");
        limpiarEstilosErrorProducto();
    }

    private void mostrarMensajeProducto(String mensaje, boolean esError) {

        lblMensajeProducto.setText(mensaje);
        if (esError) {
            lblMensajeProducto.setStyle("-fx-text-fill: red;");
        }
        else {
            lblMensajeProducto.setStyle("-fx-text-fill: green;");
        }
    }

    private void limpiarEstilosErrorProducto() {

        txtNombreProducto.getStyleClass().remove("text-field-error");
        txtPrecioUnitario.getStyleClass().remove("text-field-error");
        txtStockProducto.getStyleClass().remove("text-field-error");
        cbxCategoriaProducto.getStyleClass().remove("text-field-error");
        cbxUnidadMedidaProducto.getStyleClass().remove("text-field-error");

    }
}
