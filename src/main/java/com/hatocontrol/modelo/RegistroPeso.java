package com.hatocontrol.modelo;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "registros_peso")
public class RegistroPeso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "animal_id")
    private Animal animal;

    @Column(name = "peso_kg", nullable = false)
    private double pesoKg;

    @Column(name = "fecha_medicion", nullable = false)
    private LocalDate fechaMedicion;

    public RegistroPeso() {
    }

    public Long getId() {
        return id;
    }

    public Animal getAnimal() {
        return animal;
    }

    public void setAnimal(Animal animal) {
        this.animal = animal;
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
}
