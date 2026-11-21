package com.hatocontrol.modelo;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "registros_leche")
public class RegistroLeche extends RegistroDeAnimal {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "parto_id")
    private Parto parto;

    @Column(name = "fecha", nullable = false)
    private LocalDate fecha;

    @Column(name = "litros", nullable = false)
    private double litros;

    public RegistroLeche() {
    }

    public Parto getParto() {
        return parto;
    }

    public void setParto(Parto parto) {
        this.parto = parto;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public double getLitros() {
        return litros;
    }

    public void setLitros(double litros) {
        this.litros = litros;
    }

    @Override
    public LocalDate fechaDelRegistro() {
        return fecha;
    }
}
