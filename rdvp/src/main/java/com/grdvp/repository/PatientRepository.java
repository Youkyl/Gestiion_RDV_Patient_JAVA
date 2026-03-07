package com.grdvp.repository;

import com.grdvp.config.DatabaseConnection;
import com.grdvp.entity.Patient;
import com.grdvp.factory.ObjectFactory;
import com.grdvp.repository.interfaces.PatientRepositoryImpl;

import java.sql.*;
import java.time.LocalDateTime;
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
        String sql = "INSERT INTO patient (patient_code, lastname, firstname, address, phone, medical_history, email, password, birthday) VALUES (?, ?, ?, ?, ?, ?::jsonb, ?, ?, ?) RETURNING id, patient_code, created_at";
        
        try (PreparedStatement ps = db.prepareStatement(sql)) {

            ps.setString(1, patient.getPatientCode());
            ps.setString(2, patient.getLastname());
            ps.setString(3, patient.getFirstname());
            ps.setString(4, patient.getAddress());
            ps.setString(5, patient.getPhone());
            ps.setString(6, patient.getMedicalHistory() != null ? GSON.toJson(patient.getMedicalHistory()) : "[]");
            ps.setString(7, patient.getEmail());
            ps.setString(8, patient.getPassword());
            ps.setObject(9, patient.getBirthday());

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                patient.setId(rs.getInt("id"));
                patient.setPatientCode(rs.getString("patient_code"));
                patient.setCreatedAt(rs.getObject("created_at", LocalDateTime.class));
            }

            //System.out.println("Patient ID apres insertion demande : " + patient.getId());
        } catch (SQLException e) {
            throw new RuntimeException("Failed to insert patient", e);

        }
    }

    
    public void updatePersonalInformation(Patient patient) {
        Objects.requireNonNull(patient, "Patient cannot be null");
        String sql = "UPDATE patient SET lastname=?, firstname=?, address=?, phone=?, birthday=? WHERE id=?";
        
        try (PreparedStatement ps = db.prepareStatement(sql)) {
            ps.setString(1, patient.getLastname());
            ps.setString(2, patient.getFirstname());
            ps.setString(3, patient.getAddress());
            ps.setString(4, patient.getPhone());
            ps.setObject(5, patient.getBirthday());
            ps.setInt(6, patient.getId());

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update patient information", e);

        }
    }

    
    public void updateMedicalHistory(Patient patient, List<String> medicalHistory) {
        Objects.requireNonNull(patient, "Patient cannot be null");
        String sql = "UPDATE patient SET medical_history = ?::jsonb WHERE id = ?";

        try (PreparedStatement ps = db.prepareStatement(sql)) {

            ps.setString(1, GSON.toJson(medicalHistory != null ? medicalHistory : new ArrayList<>()));
            ps.setInt(2, patient.getId());

            ps.executeUpdate();

            patient.setMedicalHistory(medicalHistory);

        } catch (SQLException e) {
            throw new RuntimeException("Failed to update medical history", e);
        }
    }

    
    public Patient findByEmailAndPassword(String email, String password) {

        String sql = "SELECT id, patient_code, lastname, firstname, address, phone, medical_history, email, password, birthday, created_at FROM patient WHERE email = ? AND password = ?";

        try (PreparedStatement ps = db.prepareStatement(sql)) {

            ps.setString(1, email);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) return mapRowToPatient(rs);

        } catch (SQLException e) {
            throw new RuntimeException("Failed to select patient", e);
        }
        return null;
    }

    
    public List<Patient> findAll() {
        String sql = "SELECT id, patient_code, lastname, firstname, address, phone, medical_history, email, password, birthday, created_at FROM patient ORDER BY id";
        List<Patient> list = new ArrayList<>();

        try (PreparedStatement ps = db.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRowToPatient(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Failed to select patients", e);
        }
        return list;
    }

    
    public Patient findById(Integer id) {
        String sql = "SELECT id, patient_code, lastname, firstname, address, phone, medical_history, email, password, birthday, created_at FROM patient WHERE id = ?";
        try (PreparedStatement ps = db.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) 
                return mapRowToPatient(rs);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to select patient", e);
        }
        return null;
    }


    public int getNextPatientCodeNumber() {
        String sql = "SELECT MAX(CAST(SUBSTRING(patient_code, 5) AS INTEGER)) AS max_num FROM patient WHERE patient_code LIKE 'PAT-%'";

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


    private Patient mapRowToPatient(ResultSet rs) throws SQLException {
        Patient p = ObjectFactory.createPatient();
        p.setId(rs.getInt("id"));
        p.setPatientCode(rs.getString("patient_code"));
        p.setLastname(rs.getString("lastname"));
        p.setFirstname(rs.getString("firstname"));
        p.setAddress(rs.getString("address"));
        p.setPhone(rs.getString("phone"));
        String json = rs.getString("medical_history");
        p.setMedicalHistory(json != null && !json.isEmpty() ? GSON.fromJson(json, LIST_STRING) : new ArrayList<>());
        p.setEmail(rs.getString("email"));
        p.setPassword(rs.getString("password"));
        Date bd = rs.getDate("birthday");
        p.setBirthday(bd != null ? bd.toLocalDate() : null);
        Timestamp created = rs.getTimestamp("created_at");
        p.setCreatedAt(created != null ? created.toLocalDateTime() : null);
        return p;
    }
}
