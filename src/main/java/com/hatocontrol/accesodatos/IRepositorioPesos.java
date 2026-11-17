package com.hatocontrol.accesodatos;

import com.hatocontrol.modelo.RegistroPeso;
import java.util.List;

public interface IRepositorioPesos {

    void guardar(RegistroPeso registro);

    List<RegistroPeso> historial(Long animalId);

    boolean existenPara(Long animalId);
}
