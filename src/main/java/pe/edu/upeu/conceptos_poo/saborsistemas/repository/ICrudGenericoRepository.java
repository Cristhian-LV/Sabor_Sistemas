package pe.edu.upeu.conceptos_poo.saborsistemas.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface ICrudGenericoRepository <T,ID> extends JpaRepository<T,ID> {

}
