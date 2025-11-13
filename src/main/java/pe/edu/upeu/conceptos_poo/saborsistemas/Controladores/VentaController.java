package pe.edu.upeu.conceptos_poo.saborsistemas.Controladores;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import net.sf.jasperreports.engine.JasperPrint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import pe.edu.upeu.conceptos_poo.saborsistemas.components.*;
import pe.edu.upeu.conceptos_poo.saborsistemas.dto.ModeloDataAutocomplet;
import pe.edu.upeu.conceptos_poo.saborsistemas.dto.PersonaDto;
import pe.edu.upeu.conceptos_poo.saborsistemas.dto.SessionManager;
import pe.edu.upeu.conceptos_poo.saborsistemas.enums.TipoDocumento;
import pe.edu.upeu.conceptos_poo.saborsistemas.Exeption.ModelNotFoundException;
import pe.edu.upeu.conceptos_poo.saborsistemas.modelos.Cliente;
import pe.edu.upeu.conceptos_poo.saborsistemas.modelos.VentCarrito;
import pe.edu.upeu.conceptos_poo.saborsistemas.modelos.Venta;
import pe.edu.upeu.conceptos_poo.saborsistemas.modelos.VentaDetalle;
import pe.edu.upeu.conceptos_poo.saborsistemas.service.*;
import pe.edu.upeu.conceptos_poo.saborsistemas.utils.ConsultaDNI;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Consumer;

@Controller
public class VentaController {
    @FXML
    TextField autocompCliente, dniRuc, razonSocial, txtDireccion,
            autocompProducto, nombreProducto, codigoPro, stockPro, cantidadPro, punitPro, preTPro,
            txtBaseImp, txtIgv, txtDescuento, txtImporteT;
    @FXML
    TableView<VentCarrito> tableView;

    @FXML
    Button btnRegCliente, btnRegCarrito, btnRegVenta, btnImprimirVenta;

    @FXML
    AnchorPane miContenedor;
    Stage stage;

    AutoCompleteTextField actfC;
    ModeloDataAutocomplet lastCliente;
    AutoCompleteTextField actf;
    ModeloDataAutocomplet lastProducto;

    @Autowired
    IClienteService cs;
    @Autowired
    ProductoIService ps;
    @Autowired
    ConsultaDNI cDni;
    @Autowired
    IVentCarritoService daoC;
    @Autowired
    IUsuarioService daoU;
    @Autowired
    IVentaService daoV;
    @Autowired
    IVentaDetalleService daoVD;

    private JasperPrint jasperPrint;

    private final SortedSet<ModeloDataAutocomplet> entries=new TreeSet<>((ModeloDataAutocomplet o1, ModeloDataAutocomplet o2) ->
            o1.toString().compareToIgnoreCase(o2.toString()));
    private final SortedSet<ModeloDataAutocomplet> entriesC=new TreeSet<>((ModeloDataAutocomplet o1, ModeloDataAutocomplet o2) ->
            o1.toString().compareToIgnoreCase(o2.toString()));

    public void listarProducto(){
        entries.addAll(ps.listAutoCompletProducto());
    }

    public void listarCliente(){
        entriesC.clear();
        entriesC.addAll(cs.listAutoCompletCliente());
    }


    public void autoCompletarCliente(){
        actfC=new AutoCompleteTextField<>(entriesC, autocompCliente);
        autocompCliente.setOnKeyReleased(e->{
            lastCliente=(ModeloDataAutocomplet) actfC.getLastSelectedObject();
            if(lastCliente!=null){
                dniRuc.setText(lastCliente.getIdx());
                razonSocial.setText(lastCliente.getNameDysplay());
                listar();
            }else{
                btnRegCliente.setDisable(true);
                limpiarFormCliente();
            }
        });
    }

    public void limpiarFormCliente(){
        razonSocial.clear();
        dniRuc.clear();
        txtDireccion.clear();
    }

    public void listar(){
        tableView.getItems().clear();
        List<VentCarrito> lista=daoC.listaCarritoCliente(dniRuc.getText());
        double impoTotal=0;
        for(VentCarrito dato:lista){
            impoTotal+=Double.parseDouble(String.valueOf(dato.getPtotal()));
        }
        txtImporteT.setText(String.valueOf(impoTotal));
        double pv=impoTotal/1.18;
        txtBaseImp.setText(String.valueOf(Math.round(pv*100.0/100.0)));
        txtIgv.setText(String.valueOf(Math.round((pv*0.18)*100.0)/100.0));
        tableView.getItems().addAll(lista);
    }

    public void consultarDNIReniec(double with){
        PersonaDto p=cDni.consultarDNI(autocompCliente.getText());
        if(p!=null){
            razonSocial.setText(p.getNombre()+" "+p.getApellidoPaterno()+" "+p.getApellidoMaterno());
            dniRuc.setText(p.getDni());
            btnRegCliente.setDisable(false);
            Toast.showToast(stage, "El cliente se encontró en RENIEC para registrar debe hacer clic en Add ", 2000, with, 50);
        }else{
            btnRegCliente.setDisable(true);
            Toast.showToast(stage, "El cliente no se encontró en RENIEC y debe registrar a través del formulario de cliente", 2000, with, 50);
        }
    }

    @FXML
    public void buscarClienteCdni(){
        limpiarFormCliente();
        Stage stage= StageManager.getPrimaryStage();
        double with=stage.getMaxWidth()/2;
        if(autocompCliente.getText().length()==8 || autocompCliente.getText().length()==11 ){
            try {
                if(cs.findById(autocompCliente.getText())!=null){
                    btnRegCliente.setDisable(true);
                    Toast.showToast(stage, "El cliente si existe", 2000, with, 50);
                    return;
                }
                consultarDNIReniec(with);
            }catch (ModelNotFoundException e){
                btnRegCliente.setDisable(true);
                Toast.showToast(stage, "El cliente no existe", 2000, with, 50);
                consultarDNIReniec(with);
            }
        }else{
            btnRegCliente.setDisable(true);
            Toast.showToast(stage, "El valor debe tener 8 o 11 digitos", 2000, with, 50);
        }
    }

    public void deleteReg(VentCarrito obj){
        Alert alert=new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmación");
        alert.setHeaderText("Confirmar acción");
        alert.setContentText("¿Estas seguro de eliminar el registro?");
        Optional<ButtonType> result=alert.showAndWait();
        if(result.isPresent() && result.get()==ButtonType.OK){
            daoC.deleteById(obj.getIdCarrito());
            Stage stage= StageManager.getPrimaryStage();
            double with=stage.getMaxWidth()/2;
            Toast.showToast(stage, "Accion completada", 2000, with, 50);
        }
    }

    public void personalizarTabla(){
        TableViewHelper<VentCarrito> tableViewHelper=new TableViewHelper<>();
        LinkedHashMap<String, ColumnInfo> columns=new LinkedHashMap<>();
        columns.put("ID Prod", new ColumnInfo("producto.idProducto", 100.0));
        columns.put("Nombre Producto", new ColumnInfo("nombreProducto", 300.0));
        columns.put("Cantidad", new ColumnInfo("cantidad", 60.0));
        columns.put("P. Unitario", new ColumnInfo("punitario", 100.0));
        columns.put("P. Total", new ColumnInfo("ptotal", 100.0));

        Consumer<VentCarrito> updateAction=(VentCarrito ventCarrito) ->{
            System.out.println("Actualizar: "+ventCarrito);
        };

        Consumer<VentCarrito> deleteAction=(VentCarrito ventCarrito) ->{
            deleteReg(ventCarrito);
        };

        tableViewHelper.addColumnsInOrderWithSize(tableView, columns, updateAction, deleteAction);
        tableView.setTableMenuButtonVisible(true);
    }


    @FXML
    public void initialize(){
        Platform.runLater(()->{
            stage=(Stage) miContenedor.getScene().getWindow();
        });

        listarCliente();
        autoCompletarCliente();

        listarProducto();
        actf=new AutoCompleteTextField<>(entries, autocompProducto);
        autocompProducto.setOnKeyReleased(e->{
            lastProducto=(ModeloDataAutocomplet) actf.getLastSelectedObject();
            if(lastProducto!=null){
                nombreProducto.setText(lastProducto.getNameDysplay());
                codigoPro.setText(lastProducto.getIdx());
                String[] dato=lastProducto.getOtherData().split(":");
                punitPro.setText(dato[0]);
                stockPro.setText(dato[1]);
            }
        });

        personalizarTabla();
        btnRegCliente.setDisable(true);
        btnRegCarrito.setDisable(true);
    }

    @FXML
    public void guardarCliente(){
        Stage stage= StageManager.getPrimaryStage();
        double with=stage.getMaxWidth()/2;
        try {
            Cliente c=Cliente.builder()
                    .dniruc(dniRuc.getText())
                    .nombres(razonSocial.getText())
                    .repLegal(razonSocial.getText())
                    .tipoDocumento(TipoDocumento.DNI)
                    .build();
            cs.save(c);
            btnRegCliente.setDisable(true);
            Toast.showToast(stage, "Cliente registrado", 2000, with, 50);
            listarCliente();
            listar();
        } catch (Exception e) {
            Toast.showToast(stage, "Error al guardar cliente", 2000, with, 50);
        }
    }

    @FXML
    public void calcularPT(){
        if(!cantidadPro.getText().isEmpty() || !cantidadPro.getText().equals("")){
            double dato=Double.parseDouble(cantidadPro.getText())*Double.parseDouble(punitPro.getText());
            preTPro.setText(String.valueOf(Math.round(dato*100.0)/100.0));
            if(Double.parseDouble(cantidadPro.getText())>0){
                btnRegCarrito.setDisable(false);
            }else{
                btnRegCarrito.setDisable(true);
            }
        }else{
            btnRegCarrito.setDisable(true);
        }
    }

    @FXML
    public void registrarCarrito(){
        try {
            VentCarrito vc=VentCarrito.builder()
                    .dniruc(dniRuc.getText())
                    .producto(ps.findProductoById(Long.parseLong(codigoPro.getText())))
                    .nombreProducto(nombreProducto.getText())
                    .cantidad(Double.parseDouble(cantidadPro.getText()))
                    .punitario(Double.parseDouble(punitPro.getText()))
                    .ptotal(Double.parseDouble(preTPro.getText()))
                    .estado(1)
                    .usuario(daoU.findById(SessionManager.getInstance().getUserId()))
                    .build();

            daoC.save(vc);
            listar();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    public void registrarVenta(){
        Locale locale=new Locale("es", "es-PE");
        LocalDateTime date=LocalDateTime.now();
        DateTimeFormatter formatter=DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss", locale);
        String fechaFormateada=date.format(formatter);
        Venta to=Venta.builder()
                .cliente(cs.findById(dniRuc.getText()))
                .precioBase(Double.parseDouble(txtBaseImp.getText()))
                .igv(Double.parseDouble(txtIgv.getText()))
                .precioTotal(Double.parseDouble(txtImporteT.getText()))
                .usuario(daoU.findById(SessionManager.getInstance().getUserId()))
                .serie("V")
                .tipoDoc("Factura")
                .fechaGener(date.parse(fechaFormateada, formatter))
                .numDoc("00")
                .build();
        Venta idx=daoV.save(to);
        List<VentCarrito> datosC=daoC.listaCarritoCliente(dniRuc.getText());
        if(idx.getIdVenta()!=0 && !datosC.isEmpty()){
            for(VentCarrito car:datosC){
                VentaDetalle vd=VentaDetalle.builder()
                        .venta(idx)
                        .producto(ps.findProductoById(car.producto.getId_producto()))
                        .cantidad(car.getCantidad())
                        .descuento(0.0)
                        .pu(car.getPunitario())
                        .subtotal(car.getPtotal())
                        .build();
                daoVD.save(vd);
            }
        }
        daoC.deleteCarAll(dniRuc.getText());
        listar();

        try {
            jasperPrint=daoV.runReport(idx.getIdVenta());
            Platform.runLater(()->{
                ReportAlert reportAlert=new ReportAlert(jasperPrint);
                reportAlert.show();
            });

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
