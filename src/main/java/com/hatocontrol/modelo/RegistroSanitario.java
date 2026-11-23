package com.hatocontrol.modelo;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "registros_sanitarios")
public class RegistroSanitario extends RegistroDeAnimal {

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false)
    private TipoEventoSanitario tipo;

    @Column(name = "descripcion", nullable = false)
    private String descripcion;

    @Column(name = "fecha_programada")
    private LocalDate fechaProgramada;

    @Column(name = "fecha_realizacion")
    private LocalDate fechaRealizacion;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    private EstadoActividad estado;

    @Column(name = "observaciones")
    private String observaciones;

    public RegistroSanitario() {
    }

    public TipoEventoSanitario getTipo() {
        return tipo;
    }

    public void setTipo(TipoEventoSanitario tipo) {
        this.tipo = tipo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public LocalDate getFechaProgramada() {
        return fechaProgramada;
    }

    public void setFechaProgramada(LocalDate fechaProgramada) {
        this.fechaProgramada = fechaProgramada;
    }

    public LocalDate getFechaRealizacion() {
        return fechaRealizacion;
    }

    public void setFechaRealizacion(LocalDate fechaRealizacion) {
        this.fechaRealizacion = fechaRealizacion;
    }

    public EstadoActividad getEstado() {
        return estado;
    }

    public void setEstado(EstadoActividad estado) {
        this.estado = estado;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    // ---- Comportamiento de la vista lógica ----

    public void marcarRealizada(LocalDate fecha) {
        this.fechaRealizacion = fecha;
        this.estado = EstadoActividad.REALIZADA;
    }

    public boolean estaPendiente(LocalDate hoy) {
        return estado == EstadoActividad.PROGRAMADA && fechaProgramada.isBefore(hoy);
    }

    public boolean esProxima(LocalDate hoy, int dias) {
        return estado == EstadoActividad.PROGRAMADA
                && !fechaProgramada.isBefore(hoy)
                && !fechaProgramada.isAfter(hoy.plusDays(dias));
    }

    @Override
    public LocalDate fechaDelRegistro() {
        return fechaRealizacion != null ? fechaRealizacion : fechaProgramada;
    }
}
