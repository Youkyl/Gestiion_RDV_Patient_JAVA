package com.grdvp.repository.interfaces;

import com.grdvp.entity.Patient;
import java.util.List;

public interface PatientRepositoryImpl {

    void insertPatient(Patient patient);

    void updatePersonalInformation(Patient patient);

    void updateMedicalHistory(Patient patient, List<String> medicalHistory);

    Patient findByEmailAndPassword(String email, String password);
    
    List<Patient> findAll();

    Patient findById(Integer id);

    int getNextPatientCodeNumber();
}
