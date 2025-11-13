package pe.edu.upeu.conceptos_poo.saborsistemas.service;


import pe.edu.upeu.conceptos_poo.saborsistemas.modelos.VentCarrito;

import java.util.List;

public interface IVentCarritoService extends  ICrudGenericoService<VentCarrito,Long>{
    List<VentCarrito> listaCarritoCliente(String dni);
    void deleteCarAll(String dniruc);
}
