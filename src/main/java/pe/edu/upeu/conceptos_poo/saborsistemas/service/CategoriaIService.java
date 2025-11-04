package pe.edu.upeu.conceptos_poo.saborsistemas.service;

import pe.edu.upeu.conceptos_poo.saborsistemas.dto.ComboBoxOption;
import pe.edu.upeu.conceptos_poo.saborsistemas.modelos.Categoria;

import java.util.List;

public interface CategoriaIService extends CRUD_GenericoSefvice_Interface<Categoria, Long> {
    List<ComboBoxOption> listarCombobox();
}
