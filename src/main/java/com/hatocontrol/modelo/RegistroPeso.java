package com.hatocontrol.modelo;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "registros_peso")
public class RegistroPeso extends RegistroDeAnimal {

    @Column(name = "peso_kg", nullable = false)
    private double pesoKg;

    @Column(name = "fecha_medicion", nullable = false)
    private LocalDate fechaMedicion;

    public RegistroPeso() {
    }

    public double getPesoKg() {
        return pesoKg;
    }

    public void setPesoKg(double pesoKg) {
        this.pesoKg = pesoKg;
    }

    public LocalDate getFechaMedicion() {
        return fechaMedicion;
    }

    public void setFechaMedicion(LocalDate fechaMedicion) {
        this.fechaMedicion = fechaMedicion;
    }

    // ---- Comportamiento de la vista lógica ----

    /** Ganancia (positiva) o pérdida (negativa) respecto a la medición anterior. */
    public double diferenciaCon(RegistroPeso anterior) {
        return pesoKg - anterior.getPesoKg();
    }

    @Override
    public LocalDate fechaDelRegistro() {
        return fechaMedicion;
    }
}
