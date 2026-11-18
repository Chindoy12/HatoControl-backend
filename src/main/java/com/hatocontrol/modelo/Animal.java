package com.hatocontrol.modelo;

import jakarta.persistence.*;
import java.time.Period;
import java.time.LocalDate;

@Entity
@Table(name = "animales")
public class Animal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "finca_id")
    private Finca finca;

    @Column(name = "codigo_arete", nullable = false)
    private String codigoArete;

    @Column(name = "nombre")
    private String nombre;

    @Enumerated(EnumType.STRING)
    @Column(name = "sexo", nullable = false)
    private Sexo sexo;

    @Column(name = "fecha_nacimiento")
    private LocalDate fechaNacimiento;

    @Enumerated(EnumType.STRING)
    @Column(name = "proposito", nullable = false)
    private Proposito proposito;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parto_id")
    private Parto parto;

    public Animal() {
    }

    public Long getId() {
        return id;
    }

    public Finca getFinca() {
        return finca;
    }

    public void setFinca(Finca finca) {
        this.finca = finca;
    }

    public String getCodigoArete() {
        return codigoArete;
    }

    public void setCodigoArete(String codigoArete) {
        this.codigoArete = codigoArete;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Sexo getSexo() {
        return sexo;
    }

    public void setSexo(Sexo sexo) {
        this.sexo = sexo;
    }

    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(LocalDate fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public Proposito getProposito() {
        return proposito;
    }

    public void setProposito(Proposito proposito) {
        this.proposito = proposito;
    }

    public Parto getParto() {
        return parto;
    }

    public void setParto(Parto parto) {
        this.parto = parto;
    }

    // ---- Comportamiento de la vista lógica ----

    /** Edad en meses cumplidos; null si no hay fecha de nacimiento («sin registros»). */
    public Integer calcularEdadMeses(LocalDate hoy) {
        if (fechaNacimiento == null) {
            return null;
        }
        return (int) Period.between(fechaNacimiento, hoy).toTotalMonths();
    }

    /** Macho: engorde o padrón. Hembra: engorde o leche. */
    public boolean admiteProposito(Proposito p) {
        if (p == null || sexo == null) {
            return false;
        }
        return switch (p) {
            case ENGORDE -> true;
            case PADRON -> sexo == Sexo.MACHO;
            case LECHE -> sexo == Sexo.HEMBRA;
        };
    }

    public void pasarAEngorde() {
        this.proposito = Proposito.ENGORDE;
    }

    public boolean esHembraDeLeche() {
        return sexo == Sexo.HEMBRA && proposito == Proposito.LECHE;
    }
}
