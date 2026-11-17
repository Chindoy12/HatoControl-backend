package com.hatocontrol.accesodatos;

import com.hatocontrol.modelo.Animal;
import java.util.List;
import java.util.Optional;

public interface IRepositorioAnimales {

    void guardar(Animal animal);

    Optional<Animal> buscarPorArete(Long fincaId, String codigo);

    List<Animal> listar(Long fincaId, String filtro);

    void eliminar(Animal animal);
}
