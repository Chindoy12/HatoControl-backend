package com.hatocontrol.accesodatos;

import com.hatocontrol.modelo.TokenRecuperacion;
import java.util.Optional;

public interface IRepositorioTokens {

    void guardar(TokenRecuperacion token);

    Optional<TokenRecuperacion> buscarVigente(String codigo);

    void marcarUsado(TokenRecuperacion token);
}
