package pe.edu.upeu.conceptos_poo.saborsistemas.service.imp;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pe.edu.upeu.conceptos_poo.saborsistemas.dto.ComboBoxOption;
import pe.edu.upeu.conceptos_poo.saborsistemas.modelos.Categoria;
import pe.edu.upeu.conceptos_poo.saborsistemas.repository.ICategoriaRepository;
import pe.edu.upeu.conceptos_poo.saborsistemas.repository.ICrudGenericoRepository;
import pe.edu.upeu.conceptos_poo.saborsistemas.service.CategoriaIService;

import java.util.ArrayList;
import java.util.List;
@Transactional
@RequiredArgsConstructor
@Service
public class CategoriaImp extends CRUD_GenericoServiceImp<Categoria, Long> implements CategoriaIService {

    private final ICategoriaRepository categoriaRepository;

    @Override
    protected ICrudGenericoRepository<Categoria, Long> getRepository() {
        return categoriaRepository;
    }

    @Override
    public List<ComboBoxOption> listarCombobox() {
        List<ComboBoxOption> listar=new ArrayList<>();
        ComboBoxOption cb;
        for(Categoria cate : categoriaRepository.findAll()) {
            cb=new ComboBoxOption();
            cb.setKey(String.valueOf(cate.getId_categoria()));
            cb.setValue(cate.getNombre());
            listar.add(cb);
        }
        return listar;
    }
}
