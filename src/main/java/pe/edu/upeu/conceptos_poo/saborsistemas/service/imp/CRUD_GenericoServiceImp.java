package pe.edu.upeu.conceptos_poo.saborsistemas.service.imp;

import pe.edu.upeu.conceptos_poo.saborsistemas.Exeption.ModelNotFoundException;
import pe.edu.upeu.conceptos_poo.saborsistemas.repository.ICrudGenericoRepository;
import pe.edu.upeu.conceptos_poo.saborsistemas.service.CRUD_GenericoSefvice_Interface;

import java.util.List;

public abstract class CRUD_GenericoServiceImp <T,ID> implements CRUD_GenericoSefvice_Interface<T,ID> {

    protected abstract ICrudGenericoRepository<T, ID> getRepository();
    @Override
    public T save(T t) {
        return getRepository().save(t);
    }

    @Override
    public T update(ID id, T t) {
        getRepository().findById(id).orElseThrow(()-> new ModelNotFoundException("ID NOT FOUND:"+id));
        return  getRepository().save(t);
    }

    @Override
    public List<T> findAll() {
        return getRepository().findAll();
    }

    @Override
    public T findById(ID id) {
        return getRepository().findById(id).orElseThrow(()-> new ModelNotFoundException("ID NOT FOUND:"+id));
    }

    @Override
    public void delete(ID id) {
        if(!getRepository().existsById(id)){
            throw new ModelNotFoundException("ID NOT EXIST:"+id);
        }
        getRepository().deleteById(id);
    }
}
