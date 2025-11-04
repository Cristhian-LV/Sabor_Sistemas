package pe.edu.upeu.conceptos_poo.saborsistemas.service.imp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.edu.upeu.conceptos_poo.saborsistemas.dto.ModeloDataAutocomplet;
import pe.edu.upeu.conceptos_poo.saborsistemas.modelos.Producto;
import pe.edu.upeu.conceptos_poo.saborsistemas.repository.IProductoRepository;
import pe.edu.upeu.conceptos_poo.saborsistemas.service.ProductoIService;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProductoImp implements ProductoIService {
    private static final Logger logger = LoggerFactory.getLogger(ProductoImp.class);
    @Autowired
    IProductoRepository P_Repositorio;
    @Override
    public Producto saveProducto(Producto producto) {
        return P_Repositorio.save(producto);
    }

    @Override
    public List<Producto> findAllProductos() {
        return P_Repositorio.findAll();
    }

    @Override
    public Producto updateProducto(Producto producto) {
        return P_Repositorio.save(producto);
    }

    @Override
    public void deleteProductoById(Long id) {
        P_Repositorio.deleteById(id);
    }

    @Override
    public Producto findProductoById(Long id) {
        return P_Repositorio.findById(id).orElse(null);
    }

    @Override
    public List<ModeloDataAutocomplet> listAutoCompletProducto(String nombre) {
        List<ModeloDataAutocomplet> listarProducto = new ArrayList<>();
        try {
            for (Producto producto :
                    P_Repositorio.listAutoCompletProducto(nombre + "%")) {
                ModeloDataAutocomplet data = new ModeloDataAutocomplet();data.setIdx(producto.getNombre());
                data.setNameDysplay(String.valueOf(producto.getId_producto()));
                data.setOtherData(producto.getPrecioU() + ":" +
                        producto.getStok());
                listarProducto.add(data);
            }
        } catch (Exception e) {
            logger.error("Error al realizar la busqueda", e);
        }
        return listarProducto;
    }

    @Override
    public List<ModeloDataAutocomplet> listAutoCompletProducto() {
        List<ModeloDataAutocomplet> listarProducto = new ArrayList<>();
        try {
            for (Producto producto : P_Repositorio.findAll())
            {ModeloDataAutocomplet data = new ModeloDataAutocomplet();
                data.setIdx(String.valueOf(producto.getId_producto()));
                data.setNameDysplay(producto.getNombre());
                data.setOtherData(producto.getPrecioU() + ":" +
                        producto.getStok());
                listarProducto.add(data);
            }
        } catch (Exception e) {
            logger.error("Error al realizar la busqueda", e);
        }
        return listarProducto;
    }
}
