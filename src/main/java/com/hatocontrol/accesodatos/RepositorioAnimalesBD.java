package com.hatocontrol.accesodatos;

import com.hatocontrol.modelo.Animal;
import java.util.List;
import java.util.Optional;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

@Repository
public class RepositorioAnimalesBD implements IRepositorioAnimales {

    @PersistenceContext
    private EntityManager em;

    @Override
    public void guardar(Animal animal) {
        if (animal.getId() == null) {
            em.persist(animal);
        } else {
            em.merge(animal);
        }
    }

    @Override
    public Optional<Animal> buscarPorArete(Long fincaId, String codigo) {
        return em.createQuery("select a from Animal a where a.finca.id = :finca and upper(a.codigoArete) = upper(:codigo)", Animal.class)
                .setParameter("finca", fincaId)
                .setParameter("codigo", codigo)
                .getResultStream().findFirst();
    }

    @Override
    public List<Animal> listar(Long fincaId, String filtro) {
        String texto = filtro == null ? "" : filtro.trim().toLowerCase();
        return em.createQuery("""
                select a from Animal a where a.finca.id = :finca
                and (:texto = '' or lower(a.codigoArete) like :patron or lower(coalesce(a.nombre, '')) like :patron)
                order by a.codigoArete""", Animal.class)
                .setParameter("finca", fincaId)
                .setParameter("texto", texto)
                .setParameter("patron", "%" + texto + "%")
                .getResultList();
    }

    @Override
    public void eliminar(Animal animal) {
        em.remove(em.contains(animal) ? animal : em.merge(animal));
    }
}
