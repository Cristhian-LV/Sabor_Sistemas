package pe.edu.upeu.conceptos_poo.saborsistemas.service.imp;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pe.edu.upeu.conceptos_poo.saborsistemas.dto.ModeloDataAutocomplet;
import pe.edu.upeu.conceptos_poo.saborsistemas.modelos.Cliente;
import pe.edu.upeu.conceptos_poo.saborsistemas.repository.ClienteRepository;
import pe.edu.upeu.conceptos_poo.saborsistemas.repository.ICrudGenericoRepository;
import pe.edu.upeu.conceptos_poo.saborsistemas.service.IClienteService;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ClienteServiceImp extends CrudGenericoServiceImp<Cliente,String> implements IClienteService {
    private final ClienteRepository clienteRepository;

    Logger logger= LoggerFactory.getLogger(ClienteServiceImp.class);

    @Override
    protected ICrudGenericoRepository<Cliente, String> getRepo() {
        return clienteRepository;
    }

    @Override
    public List<ModeloDataAutocomplet> listAutoCompletCliente() {
        List<ModeloDataAutocomplet> listarclientes = new ArrayList<>();
        try {
            for (Cliente cliente : clienteRepository.findAll()) {
                ModeloDataAutocomplet data = new ModeloDataAutocomplet();
                data.setIdx(cliente.getDniruc());
                data.setNameDysplay(cliente.getNombres());
                data.setOtherData(cliente.getTipoDocumento().name());
                listarclientes.add(data);
            }
        } catch (Exception e) {
            logger.error("Error durante la operación", e);
        }
        return listarclientes;
    }


}
