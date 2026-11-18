package com.hatocontrol.modelo;

import jakarta.persistence.*;
import java.util.List;
import java.util.ArrayList;
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

    /** Crías nacidas en este parto (relación «nacen en»). */
    @OneToMany(mappedBy = "parto")
    @OrderBy("codigoArete")
    private List<Animal> crias = new ArrayList<>();

    /** Leche registrada en la lactancia de este parto. */
    @OneToMany(mappedBy = "parto")
    private List<RegistroLeche> registrosLeche = new ArrayList<>();

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

    public List<Animal> getCrias() {
        return crias;
    }

    public List<RegistroLeche> getRegistrosLeche() {
        return registrosLeche;
    }

    // ---- Comportamiento de la vista lógica ----

    /** Litros totales de la lactancia divididos entre los días con registro; 0 si no hay registros. */
    public double promedioDiarioLeche() {
        return registrosLeche.stream().mapToDouble(RegistroLeche::getLitros).average().orElse(0);
    }
}
