package com.hatocontrol.accesodatos;

import com.hatocontrol.modelo.EstadoActividad;
import com.hatocontrol.modelo.RegistroSanitario;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

@Repository
public class RepositorioSanitarioBD implements IRepositorioSanitario {

    @PersistenceContext
    private EntityManager em;

    @Override
    public void guardar(RegistroSanitario registro) {
        if (registro.getId() == null) {
            em.persist(registro);
        } else {
            em.merge(registro);
        }
    }

    @Override
    public Optional<RegistroSanitario> buscarPorId(Long id) {
        return em.createQuery("select r from RegistroSanitario r join fetch r.animal a join fetch a.finca where r.id = :id", RegistroSanitario.class)
                .setParameter("id", id)
                .getResultStream().findFirst();
    }

    @Override
    public List<RegistroSanitario> historial(Long animalId) {
        return em.createQuery("""
                select r from RegistroSanitario r where r.animal.id = :animal
                order by coalesce(r.fechaRealizacion, r.fechaProgramada) desc, r.id desc""", RegistroSanitario.class)
                .setParameter("animal", animalId)
                .getResultList();
    }

    @Override
    public List<RegistroSanitario> proximas(Long fincaId, LocalDate hoy, int dias) {
        return em.createQuery("""
                select r from RegistroSanitario r join fetch r.animal a
                where a.finca.id = :finca and r.estado = :programada
                and r.fechaProgramada between :hoy and :hasta
                order by r.fechaProgramada, a.codigoArete""", RegistroSanitario.class)
                .setParameter("finca", fincaId)
                .setParameter("programada", EstadoActividad.PROGRAMADA)
                .setParameter("hoy", hoy)
                .setParameter("hasta", hoy.plusDays(dias))
                .getResultList();
    }

    @Override
    public List<RegistroSanitario> pendientes(Long fincaId, LocalDate hoy) {
        return em.createQuery("""
                select r from RegistroSanitario r join fetch r.animal a
                where a.finca.id = :finca and r.estado = :programada and r.fechaProgramada < :hoy
                order by r.fechaProgramada, a.codigoArete""", RegistroSanitario.class)
                .setParameter("finca", fincaId)
                .setParameter("programada", EstadoActividad.PROGRAMADA)
                .setParameter("hoy", hoy)
                .getResultList();
    }

    @Override
    public boolean existenPara(Long animalId) {
        return !em.createQuery("select r.id from RegistroSanitario r where r.animal.id = :animal", Long.class)
                .setParameter("animal", animalId).setMaxResults(1).getResultList().isEmpty();
    }
}
