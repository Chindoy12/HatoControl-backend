package com.hatocontrol.modelo;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "partos")
public class Parto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "madre_id")
    private Animal madre;

    @Column(name = "fecha", nullable = false)
    private LocalDate fecha;

    public Parto() {
    }

    public Long getId() {
        return id;
    }

    public Animal getMadre() {
        return madre;
    }

    public void setMadre(Animal madre) {
        this.madre = madre;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }
}
