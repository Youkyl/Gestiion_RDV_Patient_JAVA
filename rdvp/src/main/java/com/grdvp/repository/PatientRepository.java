package com.grdvp.repository;

import com.grdvp.config.DatabaseConnection;
import com.grdvp.entity.Patient;
import com.grdvp.factory.ObjectFactory;
import com.grdvp.repository.interfaces.PatientRepositoryImpl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

public class PatientRepository implements PatientRepositoryImpl {

    private static final Gson GSON = new Gson();
    private static final Type LIST_STRING = new TypeToken<List<String>>() {}.getType();

    private static PatientRepository instance;
    private Connection db;

    private PatientRepository() {
        try {
            this.db = DatabaseConnection.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static PatientRepository getInstance() {
        if (instance == null) {
            instance = new PatientRepository();
        }
        return instance;
    }

    
    public void insertPatient(Patient patient) { 
        Objects.requireNonNull(patient, "Patient cannot be null");

        EntityManager entityManager = ObjectFactory.getEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();

        try {
            transaction.begin();
            if (patient.getMedicalHistory() == null) {
                patient.setMedicalHistory(new ArrayList<>());
            }

            entityManager.persist(patient);
            entityManager.flush();

            transaction.commit();
        } catch (RuntimeException e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw e;
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw new RuntimeException("Failed to insert patient", e);
        }
    }

    
    public void updatePersonalInformation(Patient patient) {
        Objects.requireNonNull(patient, "Patient cannot be null");
        EntityManager entityManager = ObjectFactory.getEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();

        String sql = "UPDATE Patient p SET lastname = :lastname, firstname = :firstname, address = :address, phone = :phone, birthday = :birthday WHERE id = :id";

        try {
            transaction.begin();
            entityManager.createQuery(sql)
                    .setParameter("lastname", patient.getLastname())
                    .setParameter("firstname", patient.getFirstname())
                    .setParameter("address", patient.getAddress())
                    .setParameter("phone", patient.getPhone())
                    .setParameter("birthday", patient.getBirthday())
                    .setParameter("id", patient.getId())
                    .executeUpdate();
            transaction.commit();
        } catch (RuntimeException e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw e;
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw new RuntimeException("Failed to update patient information", e);
        }
    }

    
    public void updateMedicalHistory(Patient patient, List<String> medicalHistory) {
        Objects.requireNonNull(patient, "Patient cannot be null");

        EntityManager entityManager = ObjectFactory.getEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();

        String sql = "UPDATE Patient p SET medicalHistory = :medicalHistory WHERE id = :id";

        try {
            transaction.begin();
            entityManager.createQuery(sql)
                    .setParameter("medicalHistory", medicalHistory != null ? medicalHistory : new ArrayList<>())
                    .setParameter("id", patient.getId())
                    .executeUpdate();
            transaction.commit();
        } catch (RuntimeException e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw e;
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw new RuntimeException("Failed to update medical history", e);
        }
    }

    
    public Patient findByEmailAndPassword(String email, String password) {

        EntityManager entityManager = ObjectFactory.getEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        
        String sql = "SELECT p FROM Patient p WHERE p.email = :email AND p.password = :password";

        Patient pat;

        try {
            transaction.begin();
            pat = entityManager.createQuery(sql, Patient.class)
                    .setParameter("email", email)
                    .setParameter("password", password)
                    .getSingleResult();

            transaction.commit();

            // if (pat != null) {
            //     return pat;
            // }
            
        } catch (RuntimeException e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw e;
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw new RuntimeException("Failed to select patient", e);
        }
        return pat;
    }

    
    public List<Patient> findAll() {

        EntityManager entityManager = ObjectFactory.getEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();

        String sql = "SELECT p FROM Patient p ORDER BY p.id";
        List<Patient> list = new ArrayList<>();

        try {
            transaction.begin();
            TypedQuery<Patient> query = entityManager.createQuery(sql, Patient.class);
            list = query.getResultList();
            transaction.commit();
        } catch (RuntimeException e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw e;
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw new RuntimeException("Failed to select patients", e);
        }
        return list;
    }

    
    public Patient findById(Integer id) {
        EntityManager entityManager = ObjectFactory.getEntityManager();
        try {
            return entityManager.find(Patient.class, id);
        } catch (Exception e) {
            throw new RuntimeException("Error finding patient by ID", e);
        }
    }


    public int getNextPatientCodeNumber() {
        String sql = "SELECT MAX(CAS T(SUBSTRING(patient_code, 5) AS INTEGER)) AS max_num FROM patient WHERE patient_code LIKE 'PAT-%'";

        try (PreparedStatement ps = db.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                int maxNum = rs.getInt("max_num");
                return maxNum + 1;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to select patient", e);
        }
        return 1;
    }
}
