package pe.edu.upeu.conceptos_poo.saborsistemas.service.imp;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pe.edu.upeu.conceptos_poo.saborsistemas.dto.ComboBoxOption;
import pe.edu.upeu.conceptos_poo.saborsistemas.modelos.UnidadMedida;
import pe.edu.upeu.conceptos_poo.saborsistemas.repository.IUnidadMedidaRepository;
import pe.edu.upeu.conceptos_poo.saborsistemas.service.UnidadMedidaService;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UnidadMedidaImp extends CRUD_GenericoServiceImp<UnidadMedida, Long> implements UnidadMedidaService {
    private final IUnidadMedidaRepository unidadmedidaRepository;
    @Override
    protected IUnidadMedidaRepository getRepository() {
        return unidadmedidaRepository;
    }
    @Override
    public List<ComboBoxOption> listarCombobox(){
        List<ComboBoxOption> listar=new ArrayList<>();
        ComboBoxOption cb;
        for(UnidadMedida cate : unidadmedidaRepository.findAll()) {
            cb=new ComboBoxOption();
            cb.setKey(String.valueOf(cate.getId_unidad()));
            cb.setValue(cate.getNombre_Medida());
            listar.add(cb);
        }
        return listar;
    }
}
