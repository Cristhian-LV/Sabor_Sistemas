package pe.edu.upeu.conceptos_poo.saborsistemas.service.imp;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pe.edu.upeu.conceptos_poo.saborsistemas.modelos.Venta;
import pe.edu.upeu.conceptos_poo.saborsistemas.repository.ICrudGenericoRepository;
import pe.edu.upeu.conceptos_poo.saborsistemas.repository.IVentaRepository;
import pe.edu.upeu.conceptos_poo.saborsistemas.service.VentaService;
import jakarta.transaction.Transactional; // Importante para operaciones con relaciones

@Service
@RequiredArgsConstructor
public class VentaImp extends CRUD_GenericoServiceImp<Venta, Long> implements VentaService {

    private final IVentaRepository ventaRepository;

    @Override
    protected ICrudGenericoRepository<Venta, Long> getRepository() {
        return ventaRepository;
    }

    // Sobrescribimos save para asegurar la transacción si es necesario
    @Override
    @Transactional // Asegura que toda la operación (guardar venta y detalles, actualizar stock) sea atómica
    public Venta save(Venta venta) {
        // Aquí podrías añadir lógica adicional antes de guardar si fuera necesario
        return super.save(venta);
    }
}