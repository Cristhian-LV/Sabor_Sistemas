package pe.edu.upeu.conceptos_poo.saborsistemas.service;

import pe.edu.upeu.conceptos_poo.saborsistemas.dto.ModeloDataAutocomplet;
import pe.edu.upeu.conceptos_poo.saborsistemas.modelos.Cliente;

import java.util.List;

public interface IClienteService extends ICrudGenericoService<Cliente,String> {
    List<ModeloDataAutocomplet> listAutoCompletCliente();
}
