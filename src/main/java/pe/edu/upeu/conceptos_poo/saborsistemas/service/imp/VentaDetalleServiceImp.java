package pe.edu.upeu.conceptos_poo.saborsistemas.service.imp;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pe.edu.upeu.conceptos_poo.saborsistemas.modelos.VentaDetalle;
import pe.edu.upeu.conceptos_poo.saborsistemas.repository.ICrudGenericoRepository;
import pe.edu.upeu.conceptos_poo.saborsistemas.repository.VentaDetalleRepository;
import pe.edu.upeu.conceptos_poo.saborsistemas.service.IVentaDetalleService;

@RequiredArgsConstructor
@Service
public class VentaDetalleServiceImp extends CrudGenericoServiceImp<VentaDetalle, Long> implements IVentaDetalleService {
    private final VentaDetalleRepository ventaDetalleRepository;
    @Override
    protected ICrudGenericoRepository<VentaDetalle, Long> getRepo() {
        return ventaDetalleRepository;
    }
}
