package com.hatocontrol.accesodatos;

import com.hatocontrol.modelo.RegistroLeche;
import java.time.LocalDate;
import java.util.List;

public interface IRepositorioLeche {

    void guardar(RegistroLeche registro);

    boolean existeEnFecha(Long animalId, LocalDate fecha);

    List<RegistroLeche> listarEntre(Long animalId, LocalDate desde, LocalDate hasta);

    boolean existenPara(Long animalId);
}
