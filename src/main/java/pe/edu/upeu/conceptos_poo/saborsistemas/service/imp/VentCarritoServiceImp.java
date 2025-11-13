package pe.edu.upeu.conceptos_poo.saborsistemas.service.imp;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.upeu.conceptos_poo.saborsistemas.modelos.VentCarrito;
import pe.edu.upeu.conceptos_poo.saborsistemas.repository.ICrudGenericoRepository;
import pe.edu.upeu.conceptos_poo.saborsistemas.repository.VentCarritoRepository;
import pe.edu.upeu.conceptos_poo.saborsistemas.service.IVentCarritoService;

import java.util.List;

@RequiredArgsConstructor
@Service
public class VentCarritoServiceImp extends CrudGenericoServiceImp<VentCarrito, Long> implements IVentCarritoService {

    private final VentCarritoRepository carritoRepository;

    @Override
    protected ICrudGenericoRepository<VentCarrito, Long> getRepo() {
        return carritoRepository;
    }
    @Override
    public List<VentCarrito> listaCarritoCliente(String dni) {
        return carritoRepository.listaCarritoCliente(dni);
    }
    @Transactional
    @Override
    public void deleteCarAll(String dniruc) {
        carritoRepository.deleteByDniruc(dniruc);
    }


}
