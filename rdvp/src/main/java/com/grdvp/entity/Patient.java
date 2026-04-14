package com.grdvp.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "patient")
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id; //Generer automatiquement dans la base de donnees

    @Column(name = "patient_code", nullable= false)
    private String patientCode; //Generer automatiquement dans le service

    @Column(nullable = false)
    private String lastname; //Obligatoire lors de la creation d'un nouveau patient

    @Column(nullable = false)
    private String firstname; //Obligatoire lors de la creation d'un nouveau patient
    
    private String address; //Facultatif lors de la creation d'un nouveau patient

    @Column(nullable = false)
    private String phone;  //Obligatoire lors de la creation d'un nouveau patient

    @Column(name = "medical_history", columnDefinition = "jsonb")
    @Convert
    private List<String> medicalHistory; //Facultatif lors de la creation d'un nouveau patient

    @Column(nullable = false)
    private String email; //Obligatoire lors de la creation d'un nouveau patient

    @Column(nullable = false)
    private String password; //Obligatoire lors de la creation d'un nouveau patient
    
    @Column(nullable = false)
    private LocalDate birthday; //Obligatoire lors de la creation d'un nouveau patient

    @Column(name = "created_at", updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt; //Generer automatiquement dans le service || dans la base de donnees
    
    @OneToMany(mappedBy = "patient", fetch = FetchType.LAZY)
    private List<DemandeRDV> demandes;

    public Patient(
            Integer id,
            String patientCode,
            String lastname,
            String firstname,
            String address,
            String phone,
            List<String> medicalHistory,
            String email,
            String password,
            LocalDate birthday,
            LocalDateTime createdAt,
            List<DemandeRDV> demandes
    ) {
        this.id = id;
        this.patientCode = patientCode;
        this.lastname = lastname;
        this.firstname = firstname;
        this.address = address;
        this.phone = phone;
        this.medicalHistory = medicalHistory != null ? medicalHistory : new ArrayList<>();
        this.email = email;
        this.password = password;
        this.birthday = birthday;
        this.createdAt = createdAt;
        this.demandes = demandes;
    }

    public Patient(
            String lastname,
            String firstname,
            //String address,
            String phone,
            String email,
            String password
            //LocalDate birthday
    ) {
        //this.id = id;
        //this.patientCode = patientCode;
        this.lastname = lastname;
        this.firstname = firstname;
        //this.address = address;
        this.phone = phone;
        //this.medicalHistory = medicalHistory != null ? medicalHistory : new ArrayList<>();
        this.email = email;
        this.password = password;
        //this.birthday = birthday;
        //this.createdAt = createdAt;
        //this.demandes = demandes;
    }

    public Patient() {
        this.medicalHistory = new ArrayList<>();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPatientCode() {
        return patientCode;
    }

    public void setPatientCode(String patientCode) {
        this.patientCode = patientCode;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public List<String> getMedicalHistory() {
        return medicalHistory;
    }

    public void setMedicalHistory(List<String> medicalHistory) {
        this.medicalHistory = medicalHistory;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public LocalDate getBirthday() {
        return birthday;
    }

    public void setBirthday(LocalDate birthday) {
        this.birthday = birthday;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public List<DemandeRDV> getDemandes() {
        return demandes;
    }


    
public String toString() { 
    return String.format( "%s %s %s %s %s %s %s %s", 
        Objects.toString("Code patient: " + patientCode, ""),
        Objects.toString("Nom: " + lastname, ""), 
        Objects.toString("Prenom: " + firstname, ""), 
        Objects.toString("Date de naissance: " + birthday, ""),
        Objects.toString("Email: " + email, ""),
        Objects.toString("Adresse: " + address, ""), 
        Objects.toString("Telephone: " + phone, ""), 
        Objects.toString("Antecedents: " + medicalHistory, "")
        //Objects.toString("Mot de passe: " + password, ""), 
        //Objects.toString("Date de creation dun compte patient: " + createdAt, ""), 
        //Objects.toString("Demandes: " + demandes, "")
    ); 
} }