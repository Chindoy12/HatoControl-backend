package com.hatocontrol.accesodatos;

import com.hatocontrol.modelo.RegistroSanitario;
import java.time.LocalDate;
import java.util.List;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

/** Implementa IRepositorioSanitario con JPA sobre PostgreSQL. */
@Repository
public class RepositorioSanitarioBD implements IRepositorioSanitario {

    @PersistenceContext
    private EntityManager em;

    @Override
    public void guardar(RegistroSanitario registro) {
        throw new UnsupportedOperationException("Se implementa en la Fase 9");
    }

    @Override
    public List<RegistroSanitario> historial(Long animalId) {
        throw new UnsupportedOperationException("Se implementa en la Fase 9");
    }

    @Override
    public List<RegistroSanitario> proximas(Long fincaId, LocalDate hoy, int dias) {
        throw new UnsupportedOperationException("Se implementa en la Fase 9");
    }

    @Override
    public List<RegistroSanitario> pendientes(Long fincaId, LocalDate hoy) {
        throw new UnsupportedOperationException("Se implementa en la Fase 9");
    }

    @Override
    public boolean existenPara(Long animalId) {
        throw new UnsupportedOperationException("Se implementa en la Fase 9");
    }
}
