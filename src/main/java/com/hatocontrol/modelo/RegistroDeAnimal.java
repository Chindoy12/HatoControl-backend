package com.hatocontrol.modelo;

import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MappedSuperclass;
import java.time.LocalDate;

/**
 * Lo que tienen en común los registros de un animal: peso, leche y eventos sanitarios.
 * Cada subclase tiene su propia tabla; esta clase no crea ninguna (MappedSuperclass).
 */
@MappedSuperclass
public abstract class RegistroDeAnimal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "animal_id")
    private Animal animal;

    public Long getId() {
        return id;
    }

    public Animal getAnimal() {
        return animal;
    }

    public void setAnimal(Animal animal) {
        this.animal = animal;
    }

    /** La fecha que cuenta para cada tipo de registro (polimorfismo: cada subclase dice cuál es). */
    public abstract LocalDate fechaDelRegistro();

    /** Ningún registro puede quedar con fecha posterior a hoy (RF27, RF47, RF15). */
    public boolean esPosteriorA(LocalDate hoy) {
        LocalDate fecha = fechaDelRegistro();
        return fecha != null && fecha.isAfter(hoy);
    }
}
