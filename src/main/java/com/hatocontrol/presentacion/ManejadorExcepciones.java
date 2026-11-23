package com.hatocontrol.presentacion;

import com.hatocontrol.negocio.excepciones.AccesoDenegadoException;
import com.hatocontrol.negocio.excepciones.HatoControlException;
import com.hatocontrol.negocio.excepciones.RecursoNoEncontradoException;
import com.hatocontrol.negocio.excepciones.ReglaNegocioException;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class ManejadorExcepciones {

    @ExceptionHandler(HatoControlException.class)
    public ResponseEntity<Map<String, Object>> manejarError(HatoControlException e) {
        int estado = switch (e) {
            case RecursoNoEncontradoException _ -> HttpStatus.NOT_FOUND.value();
            case AccesoDenegadoException _ -> HttpStatus.FORBIDDEN.value();
            case ReglaNegocioException _ -> HttpStatus.UNPROCESSABLE_CONTENT.value();
        };
        return respuesta(estado, e.getMessage(), Map.of());
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, HttpMessageNotReadableException.class,
            MethodArgumentTypeMismatchException.class, MissingServletRequestParameterException.class})
    public ResponseEntity<Map<String, Object>> manejarValidacion(Exception e) {
        if (e instanceof MethodArgumentNotValidException invalido) {
            Map<String, String> campos = new LinkedHashMap<>();
            invalido.getBindingResult().getFieldErrors()
                    .forEach(error -> campos.putIfAbsent(error.getField(), error.getDefaultMessage()));
            String primero = campos.values().stream().findFirst().orElse("Revisa los datos.");
            return respuesta(HttpStatus.BAD_REQUEST.value(), primero, campos);
        }
        return respuesta(HttpStatus.BAD_REQUEST.value(), "Revisa los datos: hay un valor vacío o con formato inválido.", Map.of());
    }

    private static ResponseEntity<Map<String, Object>> respuesta(int estado, String mensaje, Map<String, String> campos) {
        Map<String, Object> cuerpo = new LinkedHashMap<>();
        cuerpo.put("mensaje", mensaje);
        if (!campos.isEmpty()) {
            cuerpo.put("campos", campos);
        }
        return ResponseEntity.status(estado).body(cuerpo);
    }
}
