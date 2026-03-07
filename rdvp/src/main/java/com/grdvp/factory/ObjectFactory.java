package com.grdvp.factory;

import com.grdvp.entity.Patient;
import com.grdvp.entity.DemandeRDV;
import com.grdvp.entity.Specialite;
import com.grdvp.entity.Statut;
import com.grdvp.repository.PatientRepository;
import com.grdvp.repository.DemandeRDVRepository;
import com.grdvp.service.PatientService;
import com.grdvp.view.PatientView;
import com.grdvp.service.DemandeRDVService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


public class ObjectFactory {

    
    private static PatientRepository patientRepo;
    private static DemandeRDVRepository demandeRepo;

    
    private static PatientService patientService;
    private static DemandeRDVService demandeService;

    static {
        patientRepo = PatientRepository.getInstance();
        demandeRepo = DemandeRDVRepository.getInstance();
        patientService = PatientService.getInstance();
        demandeService = DemandeRDVService.getInstance();
    }

    public static PatientView createPatientView() {
        return new PatientView();
    }

    public static Patient createPatient() {
        return new Patient();
    }

    /**
     * Crée un patient avec les informations obligatoires
     */
    public static Patient createPatient(String lastname, String firstname, String phone, String email, String password) {
        Patient patient = new Patient();
        patient.setLastname(lastname);
        patient.setFirstname(firstname);
        patient.setPhone(phone);
        patient.setEmail(email);
        patient.setPassword(password);
        patient.setMedicalHistory(new ArrayList<>());
        return patient;
    }

    /**
     * Crée un patient avec toutes les informations
     */
    public static Patient createPatient(
            String lastname,
            String firstname,
            String phone,
            String email,
            String password,
            String address,
            LocalDate birthday
    ) {
        Patient patient = createPatient(lastname, firstname, phone, email, password);
        patient.setAddress(address);
        patient.setBirthday(birthday);
        return patient;
    }

    /**
     * Crée un patient depuis la base de données avec tous les paramètres
     */
    public static Patient createPatientFromDB(
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
            LocalDateTime createdAt
    ) {
        return new Patient(
                id,
                patientCode,
                lastname,
                firstname,
                address,
                phone,
                medicalHistory,
                email,
                password,
                birthday,
                createdAt,
                null
        );
    }

    // ============== FACTORY METHODS POUR DEMANDE RDV ==============

    /**
     * Crée une demande RDV vide
     */
    public static DemandeRDV createDemandeRDV() {
        return new DemandeRDV();
    }

    /**
     * Crée une demande RDV avec les informations essentielles
     */
    public static DemandeRDV createDemandeRDV(
            Patient patient,
            Specialite specialite,
            String description
    ) {
        DemandeRDV demande = new DemandeRDV();
        demande.setPatient(patient);
        demande.setSpecialite(specialite);
        demande.setDescription(description);
        demande.setCreatedAt(LocalDateTime.now());
        demande.setStatut(Statut.EN_COURS);
        return demande;
    }

    /**
     * Crée une demande RDV avec toutes les informations
     */
    public static DemandeRDV createDemandeRDV(
            Patient patient,
            Specialite specialite,
            String description,
            Statut statut
    ) {
        DemandeRDV demande = createDemandeRDV(patient, specialite, description);
        demande.setStatut(statut);
        return demande;
    }

    /**
     * Crée une demande RDV depuis la base de données
     */
    public static DemandeRDV createDemandeRDVFromDB(
            Integer id,
            Patient patient,
            Specialite specialite,
            String description,
            LocalDateTime createdAt,
            Statut statut
    ) {
        return new DemandeRDV(patient, specialite, id, description, createdAt, statut);
    }

    // ============== FACTORY METHODS POUR LES REPOSITORIES ==============

    /**
     * Récupère l'instance unique du repository Patient
     */
    public static PatientRepository getPatientRepository() {
        return patientRepo;
    }

    /**
     * Récupère l'instance unique du repository DemandeRDV
     */
    public static DemandeRDVRepository getDemandeRDVRepository() {
        return demandeRepo;
    }

    // ============== FACTORY METHODS POUR LES SERVICES ==============

    /**
     * Récupère l'instance unique du service Patient
     */
    public static PatientService getPatientService() {
        return patientService;
    }

    /**
     * Récupère l'instance unique du service DemandeRDV
     */
    public static DemandeRDVService getDemandeRDVService() {
        return demandeService;
    }

}
