package com.project.domain;

import java.io.Serializable;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "prestecs")
public class Prestec implements Serializable {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="prestecId", unique=true, nullable=false) 
    private long prestecId;

    @ManyToOne
    @JoinColumn(name = "exemplarId", nullable = false)
    private Exemplar exemplar;

    @ManyToOne
    @JoinColumn(name = "personaId", nullable = false)
    private Persona persona;

    private LocalDate dataPrestec;
    private LocalDate dataRetornPrevista;
    private LocalDate dataRetornReal;
    private boolean actiu;

    public Prestec() {}

    public Prestec( Exemplar exemplar, Persona persona, LocalDate dataPrestec, LocalDate dataRetornPrevista) {
        this.exemplar = exemplar;
        this.persona = persona;
        this.dataPrestec = dataPrestec;
        this.dataRetornPrevista = dataRetornPrevista;
    }


    public long getPrestecId() {
        return prestecId;
    }

    public void setPrestecId(long prestecId) {
        this.prestecId = prestecId;
    }

    public Exemplar getExemplar() {
        return exemplar;
    }

    public void setExemplar(Exemplar exemplar) {
        this.exemplar = exemplar;
    }

    public Persona getPersona() {
        return persona;
    }

    public void setPersona(Persona persona) {
        this.persona = persona;
    }

    public LocalDate getDataPrestec() {
        return dataPrestec;
    }

    public void setDataPrestec(LocalDate dataPrestec) {
        this.dataPrestec = dataPrestec;
    }

    public LocalDate getDataRetornPrevista() {
        return dataRetornPrevista;
    }

    public void setDataRetornPrevista(LocalDate dataRetornPrevista) {
        this.dataRetornPrevista = dataRetornPrevista;
    }

    public LocalDate getDataRetornReal() {
        return dataRetornReal;
    }

    public void setDataRetornReal(LocalDate dataRetornReal) {
        this.dataRetornReal = dataRetornReal;
    }

    public boolean isActiu() {
        return actiu;
    }

    public void setActiu(boolean actiu) {
        this.actiu = actiu;
    }

    public boolean estaRetardat() {
        return actiu && dataRetornPrevista != null && LocalDate.now().isAfter(dataRetornPrevista);
    }

    public long getDiesRetard() {
        if (estaRetardat()) {
            return LocalDate.now().toEpochDay() - dataRetornPrevista.toEpochDay();
        }
        return 0;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Prestec[id=%d", prestecId));
        
        if (exemplar != null) {
            sb.append(String.format(", exemplar='%s'", exemplar.getCodiBarres()));
        }
        
        if (persona != null) {
            sb.append(String.format(", persona='%s'", persona.getNom()));
        }
        
        sb.append(String.format(", dataPrestec='%s'", dataPrestec));
        sb.append(String.format(", dataRetornPrevista='%s'", dataRetornPrevista));
        
        if (dataRetornReal != null) {
            sb.append(String.format(", dataRetornReal='%s'", dataRetornReal));
        }
        
        sb.append(String.format(", actiu=%s", actiu));
        
        if (estaRetardat()) {
            sb.append(String.format(", diesRetard=%d", getDiesRetard()));
        }
        
        sb.append("]");
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Prestec prestec = (Prestec) o;
        return prestecId == prestec.prestecId;
    }

    @Override
    public int hashCode() {
        return Long.hashCode(prestecId);
    }
}