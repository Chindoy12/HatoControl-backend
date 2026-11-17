package com.hatocontrol.accesodatos;

import com.hatocontrol.modelo.Animal;
import java.util.List;
import java.util.Optional;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

/** Implementa IRepositorioAnimales con JPA sobre PostgreSQL. */
@Repository
public class RepositorioAnimalesBD implements IRepositorioAnimales {

    @PersistenceContext
    private EntityManager em;

    @Override
    public void guardar(Animal animal) {
        throw new UnsupportedOperationException("Se implementa en la Fase 9");
    }

    @Override
    public Optional<Animal> buscarPorArete(Long fincaId, String codigo) {
        throw new UnsupportedOperationException("Se implementa en la Fase 9");
    }

    @Override
    public List<Animal> listar(Long fincaId, String filtro) {
        throw new UnsupportedOperationException("Se implementa en la Fase 9");
    }

    @Override
    public void eliminar(Animal animal) {
        throw new UnsupportedOperationException("Se implementa en la Fase 9");
    }
}
