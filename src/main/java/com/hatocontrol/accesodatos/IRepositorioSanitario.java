package com.hatocontrol.accesodatos;

import com.hatocontrol.modelo.RegistroSanitario;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface IRepositorioSanitario {

    void guardar(RegistroSanitario registro);

    Optional<RegistroSanitario> buscarPorId(Long id);

    List<RegistroSanitario> historial(Long animalId);

    List<RegistroSanitario> proximas(Long fincaId, LocalDate hoy, int dias);

    List<RegistroSanitario> pendientes(Long fincaId, LocalDate hoy);

    boolean existenPara(Long animalId);
}
