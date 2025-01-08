package com.project.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "persones")
public class Persona implements Serializable {
    
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="personaId", unique=true, nullable=false) 
    private long personaId;

    @Column(unique = true, nullable = false)
    private String dni;

    private String nom;
    private String telefon;
    private String email;

    @OneToMany(fetch = FetchType.EAGER,mappedBy = "persona", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Prestec> prestecs = new ArrayList<>();

    public Persona() {}

    public Persona( String dni, String nom, String telefon, String email) {
        this.dni = dni;
        this.nom = nom;
        this.telefon = telefon;
        this.email = email;
    }



    public long getPersonaId() {
        return personaId;
    }

    public void setPersonaId(long personaId) {
        this.personaId = personaId;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getTelefon() {
        return telefon;
    }

    public void setTelefon(String telefon) {
        this.telefon = telefon;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<Prestec> getPrestecs() {
        return prestecs;
    }

    public void setPrestecs(List<Prestec> prestecs) {
        this.prestecs = prestecs;
    }

    public int getNumPrestecsActius() {
        return (int) prestecs.stream().filter(Prestec::isActiu).count();
    }

    public boolean tePrestecsRetardats() {
        return prestecs.stream().anyMatch(p -> p.isActiu() && p.estaRetardat());
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Persona[id=%d, dni='%s', nom='%s'", 
            personaId, dni, nom));
        
        if (telefon != null) {
            sb.append(String.format(", tel='%s'", telefon));
        }
        if (email != null) {
            sb.append(String.format(", email='%s'", email));
        }
        
        int prestecsActius = getNumPrestecsActius();
        if (prestecsActius > 0) {
            sb.append(String.format(", prestecsActius=%d", prestecsActius));
            if (tePrestecsRetardats()) {
                sb.append(" (amb retards)");
            }
        }
        
        sb.append("]");
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Persona persona = (Persona) o;
        return personaId == persona.personaId;
    }

    @Override
    public int hashCode() {
        return Long.hashCode(personaId);
    }
}