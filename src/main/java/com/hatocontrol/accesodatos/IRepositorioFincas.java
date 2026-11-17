package com.hatocontrol.accesodatos;

import com.hatocontrol.modelo.Finca;
import java.util.Optional;

public interface IRepositorioFincas {

    void guardar(Finca finca);

    Optional<Finca> buscarPorId(Long id);
}
