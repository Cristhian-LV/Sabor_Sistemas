package pe.edu.upeu.conceptos_poo.saborsistemas.service;

import pe.edu.upeu.conceptos_poo.saborsistemas.dto.ModeloDataAutocomplet;
import pe.edu.upeu.conceptos_poo.saborsistemas.modelos.Producto;

import java.util.List;

public interface ProductoIService {
    Producto saveProducto(Producto producto); // Crear o guardar un producto
    List<Producto>findAllProductos(); // Listar todos los productos
    Producto updateProducto(Producto producto); // Actualizar un producto
    void deleteProductoById(Long id); // Eliminar un producto por ID
    Producto findProductoById(Long id); // Buscar un producto por ID


    List<ModeloDataAutocomplet> listAutoCompletProducto(String nombre);
    public List<ModeloDataAutocomplet> listAutoCompletProducto();
}
