package com.hatocontrol.accesodatos;

import com.hatocontrol.modelo.Parto;
import java.util.List;

public interface IRepositorioPartos {

    void guardar(Parto parto);

    List<Parto> listarPorMadre(Long animalId);

    boolean existenPara(Long animalId);
}
