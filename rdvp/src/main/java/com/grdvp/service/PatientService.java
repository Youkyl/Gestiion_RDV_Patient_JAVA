package com.grdvp.service;

import com.grdvp.entity.Patient;
import com.grdvp.factory.ObjectFactory;
import com.grdvp.repository.PatientRepository;
import com.grdvp.repository.interfaces.PatientRepositoryImpl;
import com.grdvp.service.interfaces.PatientServiceImpl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PatientService implements PatientServiceImpl {

    private static PatientService instance;
    private final PatientRepositoryImpl patientRepo;

    private PatientService() {
        this.patientRepo = PatientRepository.getInstance();
    }

    public static PatientService getInstance() {
        if (instance == null) {
            instance = new PatientService();
        }
        return instance;
    }

    public Patient createPatientCode(String lastname, String firstname, String phone, String email, String password) 
    {
        Patient patient = ObjectFactory.createPatient();
        patient.setLastname(lastname);
        patient.setFirstname(firstname);
        patient.setPhone(phone);
        patient.setEmail(email);
        patient.setPassword(password);
        
        return patient;
    }

    
    public void addPatient(Patient patient) {
        if (patient.getPatientCode() == null || patient.getPatientCode().isEmpty()) {
            patient.setPatientCode(generatePatientCode());
        }
        if (patient.getCreatedAt() == null) {
            patient.setCreatedAt(LocalDateTime.now());
        }
        patientRepo.insertPatient(patient);
    }

   
    public void addPersonalInformation(Patient patient) {
        patientRepo.updatePersonalInformation(patient);
    }

    
    public void addMedicalHistory(Patient patient, List<String> medicalHistory) {
        patientRepo.updateMedicalHistory(patient, medicalHistory);
    }

   
    public String generatePatientCode() {
        int nextNumber = patientRepo.getNextPatientCodeNumber(); 
        return String.format("PAT-%04d", nextNumber);
    }

   
    public Patient connexion(String email, String password) {
        return patientRepo.findByEmailAndPassword(email, password);
    }

    
    public void completePatientInfo(Patient patient, LocalDate dateNaissance, String adresse) {
        patient.setBirthday(dateNaissance);
        patient.setAddress(adresse);
        patientRepo.updatePersonalInformation(patient);
    }

   
    public void addAntecedent(Patient patient, String antecedent) {
        List<String> history = patient.getMedicalHistory() != null ? new ArrayList<>(patient.getMedicalHistory()) : new ArrayList<>();
        history.add(antecedent);
        patient.setMedicalHistory(history);
        patientRepo.updateMedicalHistory(patient, history);
    }

    
    public Patient getConnectedPatientInfo(Integer patientId) {
        return patientRepo.findById(patientId);
    }
}
