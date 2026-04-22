package com.grdvp.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "demande_rdv")
public class DemandeRDV {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;


    @Column(nullable = false)
    private String description;

    @Column(name = "created_at", updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "specialite", nullable = false, columnDefinition = "specialite_enum")
    private Specialite specialite;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "statut", columnDefinition = "statut_enum", nullable = false)
    private Statut statut;

    public DemandeRDV() {}


    public DemandeRDV(
        Patient patient,
        Specialite specialite,
        Integer id,
        String description,
        LocalDateTime createdAt,
        Statut statut)
    {
        this.id = id;
        this.description = description;
        this.createdAt = createdAt;
        this.patient = patient;
        this.specialite = specialite;
        this.statut = statut;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getPatientId() {
        return patient != null ? patient.getId() : null;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Patient getPatient() {
        return patient;
    }


    public Specialite getSpecialite() {
        return specialite;
    }

    public void setSpecialite(Specialite specialite) {
        this.specialite = specialite;
    }

    public Statut getStatut() {
        return statut;
    }

    public void setStatut(Statut statut) {
        this.statut = statut;
    }

    
    public String toString() {
        String createdAtString = (createdAt != null) ? createdAt.toString() : "null";
        String patientString = (patient != null) ? patient.toString() : "Patient";
        String specialiteString = (specialite != null) ? specialite.toString() : "Specialite";
        String statutString = (statut != null) ? statut.toString() : "null";
        return String.format(
            "DemandeRDV { id: %d, description: %s, createdAt: %s, patient: %s, specialite: %s, statut: %s }",
            id,
            (description != null) ? description : "null",
            createdAtString,
            patientString,
            specialiteString,
            statutString
        );
    }


    public void setPatient(Patient patient) {
        this.patient = patient;
    }
}