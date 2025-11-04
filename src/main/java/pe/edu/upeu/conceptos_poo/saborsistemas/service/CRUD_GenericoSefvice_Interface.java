package pe.edu.upeu.conceptos_poo.saborsistemas.service;

import java.util.List;

public interface CRUD_GenericoSefvice_Interface <T,ID> {
    T save(T entity);
    T update(ID id, T entity);
    List<T> findAll();
    T findById(ID id);
    void delete(ID id);

}
