package pe.edu.upeu.conceptos_poo.saborsistemas.Controladores;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.context.annotation.Scope;

// Modelos
import pe.edu.upeu.conceptos_poo.saborsistemas.modelos.Producto;
import pe.edu.upeu.conceptos_poo.saborsistemas.modelos.Usuario;
import pe.edu.upeu.conceptos_poo.saborsistemas.modelos.Venta;
import pe.edu.upeu.conceptos_poo.saborsistemas.modelos.DetalleVenta;

// Servicios
import pe.edu.upeu.conceptos_poo.saborsistemas.service.ProductoIService;
import pe.edu.upeu.conceptos_poo.saborsistemas.service.UsuarioService;
import pe.edu.upeu.conceptos_poo.saborsistemas.service.VentaService;

import pe.edu.upeu.conceptos_poo.saborsistemas.dto.SessionManager;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Controller
@Scope("prototype") // Cada vez que se abra la pestaña de venta, será una nueva instancia
public class VentaController {

    // --- Servicios Inyectados ---
    @Autowired private VentaService ventaService;
    @Autowired private ProductoIService productoService;
    @Autowired private UsuarioService usuarioService;
    @Autowired private ConfigurableApplicationContext applicationContext; // <-- NUEVA LÍNEA
    // @Autowired private ClienteService clienteService; // Descomenta si buscas/guardas clientes

    // ===== Encabezado (Buscar) =====
    @FXML private TextField txtFiltroDato;
    @FXML private Label idPrueba; // opcional

    // ===== Datos del cliente =====
    @FXML private TextField txtClienteNombre;
    @FXML private TextField txtClienteApellido;
    @FXML private TextField txtClienteDni;

    // ===== Pago =====
    @FXML private ComboBox<String> cbMetodoPago;
    @FXML private TextField txtNumTarjeta;
    @FXML private TextField txtCvv;

    // ===== Línea de producto =====
    @FXML private TextField txtNombreProducto;
    @FXML private TextField txtPrecioUnitario;
    @FXML private TextField txtCantidad;
    @FXML private TextField txtTotal; // Total general de la tabla
    @FXML private Label lbnMsg;

    // ===== Tabla =====
    @FXML private TableView<VentaItem> tablaVenta;
    @FXML private TableColumn<VentaItem, String>  colProducto; // Muestra el Nombre del producto
    @FXML private TableColumn<VentaItem, Double>  colPrecio;
    @FXML private TableColumn<VentaItem, Integer> colCantidad;
    @FXML private TableColumn<VentaItem, Double>  colSubtotal;

    // Lista observable para la tabla de items de venta
    private final ObservableList<VentaItem> listaVenta = FXCollections.observableArrayList();

    // Variable para guardar temporalmente el ID del producto seleccionado
    private Long idProductoSeleccionado = null;

    @FXML
    public void initialize() {
        // Configuración de la Tabla
        colProducto.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getNombreProducto()));
        colPrecio.setCellValueFactory(d -> new SimpleDoubleProperty(d.getValue().getPrecioUnitario()).asObject());
        colCantidad.setCellValueFactory(d -> new SimpleIntegerProperty(d.getValue().getCantidad()).asObject());
        colSubtotal.setCellValueFactory(d -> new SimpleDoubleProperty(d.getValue().getSubtotal()).asObject());
        tablaVenta.setItems(listaVenta);

        // Configuración ComboBox Método de Pago
        if (cbMetodoPago != null) {
            cbMetodoPago.setItems(FXCollections.observableArrayList("Efectivo", "Débito", "Crédito"));
            cbMetodoPago.getSelectionModel().selectFirst(); // Selecciona "Efectivo" por defecto
            cbMetodoPago.valueProperty().addListener((obs, oldVal, newVal) -> toggleCamposTarjeta());
            toggleCamposTarjeta(); // Ejecuta una vez al inicio para deshabilitar campos de tarjeta
        }

        // Listener para calcular subtotal al cambiar cantidad o precio (si se editan)
        // (Opcional, si permites editar directamente en los textfields)
        txtCantidad.textProperty().addListener((obs, ov, nv) -> calcularSubtotalItem());
        txtPrecioUnitario.textProperty().addListener((obs, ov, nv) -> calcularSubtotalItem());

        actualizarTotalGeneral(); // Calcula el total inicial (0.00)
    }

    // ===== Botones del FXML =====

    @FXML
    private void buscarProducto() {
        String q = nvl(txtFiltroDato != null ? txtFiltroDato.getText() : "");
        if (q.isBlank()) { info("Escribe algo para buscar (nombre o ID)."); return; }
        try {
            Optional<Producto> productoOpt = Optional.empty();
            // Intenta buscar por ID primero
            try {
                Long idBusqueda = Long.parseLong(q);
                Producto p = productoService.findProductoById(idBusqueda);
                if (p != null) productoOpt = Optional.of(p);
            } catch (NumberFormatException nfe) {
                // Si no es un número, busca por nombre (primera coincidencia)
                List<Producto> encontrados = productoService.findAllProductos().stream()
                        .filter(p -> p.getNombre().toLowerCase().contains(q.toLowerCase()))
                        .collect(Collectors.toList());
                if (!encontrados.isEmpty()) productoOpt = Optional.of(encontrados.get(0));
            }


            if (productoOpt.isPresent()) {
                Producto p = productoOpt.get();
                idProductoSeleccionado = p.getId_producto(); // Guarda el ID
                txtNombreProducto.setText(p.getNombre());
                txtPrecioUnitario.setText(String.format("%.2f", p.getPrecioU()));
                txtCantidad.setText("1"); // Pone 1 por defecto
                info("Producto encontrado: " + p.getNombre());
                calcularSubtotalItem(); // Calcula subtotal para el item
            } else {
                info("Producto no encontrado.");
                limpiarCamposProducto(); // Limpia si no encuentra
            }
        } catch (Exception e) {
            warn("Error al buscar producto: " + e.getMessage());
            e.printStackTrace();
            limpiarCamposProducto();
        }
    }


    @FXML
    private void agregarProducto() {
        if (idProductoSeleccionado == null || nvl(txtNombreProducto.getText()).isBlank()) {
            warn("Busca y selecciona un producto válido primero.");
            return;
        }

        int cant;
        try {
            cant = Integer.parseInt(nvl(txtCantidad.getText()));
            if (cant <= 0) throw new NumberFormatException();
        } catch (Exception e) { warn("Cantidad inválida (debe ser entero > 0)."); return; }

        double precio;
        try {
            precio = Double.parseDouble(nvl(txtPrecioUnitario.getText()));
            if (precio <= 0) throw new NumberFormatException();
        } catch (Exception e) { warn("Precio inválido (debe ser número > 0)."); return; }

        // Verificar Stock ANTES de agregar a la tabla
        try {
            Producto p = productoService.findProductoById(idProductoSeleccionado);
            if (p == null) {
                warn("Error: El producto seleccionado ya no existe.");
                limpiarCamposProducto();
                return;
            }
            // Sumar la cantidad si el producto ya está en la tabla
            int cantidadActualEnTabla = listaVenta.stream()
                    .filter(item -> item.getIdProducto().equals(idProductoSeleccionado))
                    .mapToInt(VentaItem::getCantidad)
                    .sum();

            if ((cantidadActualEnTabla + cant) > p.getStok()) {
                warn("Stock insuficiente para '" + p.getNombre() + "'. Stock disponible: " + p.getStok() + ", ya en carrito: " + cantidadActualEnTabla);
                return;
            }

            // Si ya existe, actualiza cantidad y subtotal en lugar de añadir
            Optional<VentaItem> itemExistente = listaVenta.stream()
                    .filter(item -> item.getIdProducto().equals(idProductoSeleccionado))
                    .findFirst();

            if (itemExistente.isPresent()) {
                VentaItem item = itemExistente.get();
                item.setCantidad(item.getCantidad() + cant);
                item.setSubtotal(item.getCantidad() * item.getPrecioUnitario()); // Recalcular subtotal
                tablaVenta.refresh(); // Refrescar la tabla para mostrar el cambio
            } else {
                // Añadir nuevo item a la tabla
                double sub = cant * precio;
                VentaItem newItem = new VentaItem(idProductoSeleccionado, txtNombreProducto.getText(), precio, cant, sub);
                listaVenta.add(newItem);
            }

            limpiarCamposProducto();
            actualizarTotalGeneral();
            info("Producto agregado/actualizado en la venta.");

        } catch (Exception e) {
            warn("Error al verificar stock: " + e.getMessage());
            e.printStackTrace();
        }
    }


    @FXML
    private void eliminarProducto() {
        VentaItem sel = tablaVenta.getSelectionModel().getSelectedItem();
        if (sel == null) { warn("Selecciona una fila para eliminar."); return; }
        listaVenta.remove(sel);
        actualizarTotalGeneral();
        info("Producto eliminado de la lista.");
    }

    @FXML
    private void vender() {
        // Validaciones de Cliente
        if (nvl(txtClienteNombre.getText()).isBlank()
                || nvl(txtClienteApellido.getText()).isBlank()
                || nvl(txtClienteDni.getText()).isBlank()) {
            warn("Completa Nombre, Apellido y DNI."); return;
        }
        String dniCliente = nvl(txtClienteDni.getText());

        // Validación de Lista de Venta
        if (listaVenta.isEmpty()) { warn("Agrega al menos un producto."); return; }

        // Validaciones de Pago
        String mp = cbMetodoPago.getValue(); // No necesita nvl si siempre hay uno seleccionado
        boolean esTarjeta = "Débito".equals(mp) || "Crédito".equals(mp);
        String nt = null;
        if (esTarjeta) {
            nt = nvl(txtNumTarjeta.getText());
            String cvv = nvl(txtCvv.getText()); // CVV no se guarda en BD generalmente, solo se valida
            if (nt.length() != 16 || !nt.matches("\\d{16}")) { warn("N° de tarjeta inválido (16 dígitos)."); return; }
            if (cvv.length() != 3 || !cvv.matches("\\d{3}")) { warn("CVV inválido (3 dígitos)."); return; }
        }

        try {
            // --- LÓGICA DE GUARDADO ---
            Venta nuevaVenta = new Venta();
            nuevaVenta.setDniCliente(dniCliente); // Guardamos DNI
            nuevaVenta.setNombreCliente(nvl(txtClienteNombre.getText()));
            nuevaVenta.setApellidoCliente(nvl(txtClienteApellido.getText()));

            nuevaVenta.setMetodoPago(mp);
            nuevaVenta.setNumeroTarjeta(nt);
            nuevaVenta.setFechaVenta(LocalDateTime.now());

            Usuario usuarioLogueado = obtenerUsuarioActual();
            if (usuarioLogueado == null) {
                warn("Error: Sesión de usuario inválida. Por favor, inicia sesión de nuevo.");
                // Aquí podrías redirigir al login
                return;
            }
            nuevaVenta.setUsuario(usuarioLogueado);

            List<DetalleVenta> detalles = new ArrayList<>();
            double montoTotalCalculado = 0;

            // Volver a verificar stock justo antes de guardar (importante por concurrencia)
            for (VentaItem itemTabla : listaVenta) {
                Producto producto = productoService.findProductoById(itemTabla.getIdProducto());
                if (producto == null) {
                    throw new RuntimeException("Error crítico: Producto con ID " + itemTabla.getIdProducto() + " no encontrado durante el guardado.");
                }
                if (producto.getStok() < itemTabla.getCantidad()) {
                    throw new RuntimeException("Stock insuficiente para: " + producto.getNombre() + " al momento de guardar. Stock actual: " + producto.getStok());
                }
            }

            // Crear detalles, calcular total y preparar actualización de stock
            List<Producto> productosParaActualizar = new ArrayList<>();
            for (VentaItem itemTabla : listaVenta) {
                Producto producto = productoService.findProductoById(itemTabla.getIdProducto()); // Re-obtener para asegurar datos frescos

                DetalleVenta detalle = new DetalleVenta();
                detalle.setVenta(nuevaVenta);
                detalle.setProducto(producto);
                detalle.setPrecioUnitario(itemTabla.getPrecioUnitario());
                detalle.setCantidad(itemTabla.getCantidad());
                detalle.setSubtotal(itemTabla.getSubtotal());
                detalles.add(detalle);

                montoTotalCalculado += itemTabla.getSubtotal();

                // Descontar stock (preparar para guardar)
                producto.setStok(producto.getStok() - itemTabla.getCantidad());
                productosParaActualizar.add(producto);
            }

            nuevaVenta.setDetalleVenta(detalles);
            nuevaVenta.setMontoTotal(montoTotalCalculado);

            // Guardar la venta y actualizar el stock en una transacción
            ventaService.save(nuevaVenta); // Esto guarda venta y detalles
            for (Producto p : productosParaActualizar) {
                productoService.updateProducto(p); // Actualizar stock de cada producto
            }

            generarBoleta(nuevaVenta);

            // --- FIN LÓGICA ---

            info("Venta registrada correctamente. ID: " + nuevaVenta.getIdVenta()); // Mostrar ID
            tablaVenta.getItems().clear();
            txtClienteNombre.clear();
            txtClienteApellido.clear();
            txtClienteDni.clear();
            cbMetodoPago.getSelectionModel().selectFirst();
            actualizarTotalGeneral();
            limpiarCamposProducto(); // Limpia campos de item

        } catch (Exception e) {
            warn("Error al guardar la venta: " + e.getMessage());
            e.printStackTrace();
            // Considerar revertir cambios de stock si la venta falla a mitad
        }
    }

    private void generarBoleta(Venta venta) {
        Long idVenta = venta.getIdVenta();
        // 1. Especificar el directorio 'Boletas' dentro de la carpeta actual
        String directorio = "Boletas";
        File dir = new File(directorio);
        if (!dir.exists()) {
            dir.mkdirs(); // Crea la carpeta si no existe
        }

        // 2. Combinar el directorio con el nombre del archivo
        String nombreArchivo = directorio + File.separator + "Boleta_Venta_" + idVenta + ".txt";

        try (PrintWriter writer = new PrintWriter(new FileWriter(nombreArchivo))) {

            // --- 1. CABECERA Y DATOS DE LA VENTA ---
            writer.println("=========================================");
            writer.println("           SABOR SISTEMAS");
            writer.println("           BOLETA DE VENTA N° " + idVenta);
            writer.println("=========================================");

            String fecha = venta.getFechaVenta().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
            writer.println("FECHA: " + fecha);

            // Asumiendo que obtienes el usuario de esta manera
            String vendedor = venta.getUsuario().getNombre_Usuario();
            writer.println("VENDEDOR: " + vendedor);
            writer.println("-----------------------------------------");

            // --- 2. DATOS DEL CLIENTE Y PAGO ---
            String clienteNombreCompleto = venta.getNombreCliente() + " " + venta.getApellidoCliente();
            writer.println("CLIENTE: " + clienteNombreCompleto);
            writer.println("DNI: " + venta.getDniCliente());
            writer.println("METODO DE PAGO: " + venta.getMetodoPago());
            writer.println("-----------------------------------------");

            // --- 3. DETALLE DE PRODUCTOS ---
            writer.println("DETALLES DE LA VENTA:");
            writer.printf("%-20s %-8s %-10s %-10s%n", "PRODUCTO", "CANT.", "P.UNIT.", "SUBTOTAL");
            writer.println("-----------------------------------------");

            List<DetalleVenta> detalles = venta.getDetalleVenta();
            for (DetalleVenta detalle : detalles) {
                String nombreProd = detalle.getProducto().getNombre();
                // Formato para alinear columnas
                writer.printf("%-20s %-8d %-10.2f %-10.2f%n",
                        nombreProd,
                        detalle.getCantidad(),
                        detalle.getPrecioUnitario(),
                        detalle.getSubtotal());
            }

            // --- 4. RESUMEN Y TOTAL ---
            writer.println("-----------------------------------------");
            writer.printf("MONTO TOTAL: %.2f%n", venta.getMontoTotal());
            writer.println("=========================================");
            writer.println("       ¡GRACIAS POR SU COMPRA!");

            // Muestra una notificación al usuario (depende de tu método 'info')
            info("Boleta generada en: " + nombreArchivo);

        } catch (IOException e) {
            warn("Error al generar el archivo de boleta: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void abrirSelectorProducto() {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                    getClass().getResource("/fxml/seleccionar_productos.fxml"));

            // ESTA LÍNEA ES LA CORRECCIÓN CLAVE
            loader.setControllerFactory(applicationContext::getBean);

            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Seleccionar producto");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL); // Bloquea la ventana principal
            stage.initOwner(tablaVenta.getScene().getWindow()); // Establece la ventana dueña
            stage.showAndWait(); // Espera a que se cierre la ventana del selector

            // Recuperamos el producto seleccionado del Holder
            var sel = ProductoSeleccionadoHolder.getProducto();
            if (sel != null) {
                // Buscamos el producto real por ID (o nombre si el código es nombre)
                try {
                    Producto pReal = buscarProductoPorNombre(sel.getNombre()); // O usa sel.getCodigo() si es ID
                    if (pReal != null) {
                        idProductoSeleccionado = pReal.getId_producto();
                        txtNombreProducto.setText(pReal.getNombre());
                        txtPrecioUnitario.setText(String.format("%.2f", pReal.getPrecioU()));
                        txtCantidad.setText("1"); // Default a 1
                        calcularSubtotalItem();
                    } else {
                        warn("El producto seleccionado ya no está disponible.");
                        limpiarCamposProducto();
                    }
                } catch (Exception ex) {
                    warn("Error al cargar datos del producto seleccionado.");
                    ex.printStackTrace();
                    limpiarCamposProducto();
                }

            }
            ProductoSeleccionadoHolder.limpiar(); // Limpia el holder

        } catch (Exception e) {
            e.printStackTrace();
            new Alert(
                    Alert.AlertType.ERROR,
                    "Error al abrir el selector de productos: " + e.getMessage()
            ).showAndWait();
        }
    }

    private void toggleCamposTarjeta() {
        if (cbMetodoPago == null) return; // Seguridad
        String v = cbMetodoPago.getValue();
        boolean tarjeta = "Débito".equals(v) || "Crédito".equals(v);
        if (txtNumTarjeta != null) txtNumTarjeta.setDisable(!tarjeta);
        if (txtCvv != null) txtCvv.setDisable(!tarjeta);
        if (!tarjeta) {
            if (txtNumTarjeta != null) txtNumTarjeta.clear();
            if (txtCvv != null) txtCvv.clear();
        }
    }

    // Calcula el total general sumando subtotales de la tabla
    private void actualizarTotalGeneral() {
        double total = listaVenta.stream().mapToDouble(VentaItem::getSubtotal).sum();
        if (txtTotal != null) txtTotal.setText(String.format("%.2f", total));
    }

    // Limpia los campos relacionados a un item de producto
    private void limpiarCamposProducto() {
        idProductoSeleccionado = null; // Resetea el ID seleccionado
        if (txtNombreProducto != null) txtNombreProducto.clear();
        if (txtPrecioUnitario != null) txtPrecioUnitario.clear();
        if (txtCantidad != null) txtCantidad.clear();
        if (txtFiltroDato != null) txtFiltroDato.clear(); // Limpia búsqueda también
        // No limpiamos txtTotal aquí, ese es el general
    }

    // Calcula el subtotal para el item actual en los TextFields (si aplica)
    private void calcularSubtotalItem() {
        // Podrías añadir lógica aquí si necesitas mostrar el subtotal
        // del item *antes* de agregarlo a la tabla, pero VentaController no tiene
        // un TextField para eso. Se calcula al agregar.
    }


    private Usuario obtenerUsuarioActual() {
        SessionManager sm = SessionManager.getInstance();
        if (sm != null && sm.getUserId() != null) {
            try {
                return usuarioService.findById(sm.getUserId());
            } catch (Exception e) {
                System.err.println("Error buscando usuario en sesión ID " + sm.getUserId() + ": " + e.getMessage());
                return null; // O manejar de otra forma
            }
        }
        System.err.println("SessionManager no inicializado o sin UserId.");
        return null; // No hay usuario en sesión
    }

    // Busca producto por nombre (sensible a mayúsculas/minúsculas)
    private Producto buscarProductoPorNombre(String nombre) throws Exception {
        List<Producto> productos = productoService.findAllProductos();
        for (Producto p : productos) {
            if (p.getNombre().equalsIgnoreCase(nombre)) {
                return p;
            }
        }
        return null; // No encontrado
    }


    // Muestra mensaje de advertencia y lo pone en lbnMsg
    private void warn(String m){ lbnMsgSet(m, true); new Alert(Alert.AlertType.WARNING, m, ButtonType.OK).showAndWait(); }
    // Muestra mensaje informativo y lo pone en lbnMsg
    private void info(String m){ lbnMsgSet(m, false); }
    // Actualiza la etiqueta de mensajes
    private void lbnMsgSet(String m, boolean isError){
        if (lbnMsg != null) {
            lbnMsg.setText(m);
            lbnMsg.setStyle(isError ? "-fx-text-fill: red;" : "-fx-text-fill: green;");
        }
    }
    // Helper para evitar NullPointerException con Strings y hacer trim
    private String nvl(String s){ return s==null? "" : s.trim(); }

    // ===== DTO Interno para la Tabla (VentaItem) =====
    // Se usa para mostrar los datos en la TableView antes de guardar
    public static class VentaItem {
        private final Long idProducto; // Guardamos el ID del producto
        private String nombreProducto; // Nombre para mostrar
        private double precioUnitario;
        private int cantidad;
        private double subtotal;

        public VentaItem(Long idProducto, String nombreProducto, double precioUnitario, int cantidad, double subtotal) {
            this.idProducto = idProducto;
            this.nombreProducto = nombreProducto;
            this.precioUnitario = precioUnitario;
            this.cantidad = cantidad;
            this.subtotal = subtotal;
        }

        // Getters
        public Long getIdProducto() { return idProducto; }
        public String getNombreProducto() { return nombreProducto; }
        public double getPrecioUnitario()   { return precioUnitario; }
        public int getCantidad()    { return cantidad; }
        public double getSubtotal() { return subtotal; }

        // Setters para cantidad y subtotal (si permites edición en tabla o recalculas)
        public void setCantidad(int cantidad) { this.cantidad = cantidad; }
        public void setSubtotal(double subtotal) { this.subtotal = subtotal; }
    }
}