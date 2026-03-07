package com.grdvp.repository;

import com.grdvp.config.DatabaseConnection;
import com.grdvp.entity.DemandeRDV;
import com.grdvp.entity.Specialite;
import com.grdvp.entity.Statut;
import com.grdvp.factory.ObjectFactory;
import com.grdvp.repository.interfaces.DemandeRDVRepositoryImpl;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DemandeRDVRepository implements DemandeRDVRepositoryImpl {

    private static DemandeRDVRepository instance;
    private Connection db;

    private DemandeRDVRepository() {
        try {
            this.db = DatabaseConnection.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static DemandeRDVRepository getInstance() {
        if (instance == null) {
            instance = new DemandeRDVRepository();
        }
        return instance;
    }

    
    public void insertDemande(DemandeRDV demande) {
        Objects.requireNonNull(demande, "Patient cannot be null");

        String sql = "INSERT INTO demande_rdv (description, patient_id, specialite, statut) VALUES (?, ?, ?::specialite_enum, ?::statut_enum) RETURNING id, created_at";

        try (PreparedStatement ps = db.prepareStatement(sql)) {

            ps.setString(1, demande.getDescription());

            //int patientId = demande.getPatient().getId();

            ps.setInt(2, demande.getPatient().getId());
            
            ps.setString(3, demande.getSpecialite().name());
            
            ps.setString(4, demande.getStatut() != null ? demande.getStatut().name() : Statut.EN_COURS.name());

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                demande.setId(rs.getInt("id"));
                Timestamp created = rs.getTimestamp("created_at");
                demande.setCreatedAt(created != null ? created.toLocalDateTime() : null);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error inserting demande", e);
        }
    }

    
    public List<DemandeRDV> selectDemande(int patientId) {
        return findDemandesByCondition("WHERE patient_id = ?", patientId);
    }

    
    public List<DemandeRDV> selectDemandeByStatut(String statut, int patientId) {

        String sql = (patientId == 0)
            ? "SELECT id, description, created_at, patient_id, specialite, statut FROM demande_rdv WHERE statut = ?::statut_enum ORDER BY created_at DESC"
            : "SELECT id, description, created_at, patient_id, specialite, statut FROM demande_rdv WHERE statut = ?::statut_enum AND patient_id = ? ORDER BY created_at DESC";

        List<DemandeRDV> list = new ArrayList<>();

        try (PreparedStatement ps = db.prepareStatement(sql)) {

            ps.setString(1, statut);

            if (patientId != 0) {
                ps.setInt(2, patientId);
            }

            ResultSet rs = ps.executeQuery();

            while (rs.next()) list.add(mapRowToDemande(rs));

        } catch (SQLException e) {
            throw new RuntimeException("Error selecting demandes by statut", e);
        }
        return list;
    }

    
    public List<DemandeRDV> selectAppointment(int patientId) {
        return selectDemandeByStatut(Statut.ACCEPTE.name(), patientId);
    }


    public void updateStatut(int demandeId, Statut statut) {
        Objects.requireNonNull(statut, "Statut cannot be null");
        String sql = "UPDATE demande_rdv SET statut = ?::statut_enum WHERE id = ?";

        try (PreparedStatement ps = db.prepareStatement(sql)) {

            ps.setString(1, statut.name());
            ps.setInt(2, demandeId);

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error updating demande statut", e);
        }
    }
   
    
    public DemandeRDV findById(int demandeId) {
        String sql = "SELECT id, description, created_at, patient_id, specialite, statut FROM demande_rdv WHERE id = ?";
        try (PreparedStatement ps = db.prepareStatement(sql)) {
            ps.setInt(1, demandeId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapRowToDemande(rs);
        } catch (SQLException e) {
            throw new RuntimeException("Error finding demande by ID", e);
        }
        return null;
    }


    public List<DemandeRDV> findAll() {
        return findDemandesByCondition("", null);
    }


    private List<DemandeRDV> findDemandesByCondition(String whereClause, Integer patientId) {
        String sql = (whereClause == null || whereClause.isEmpty())
            ? "SELECT id, description, created_at, patient_id, specialite, statut FROM demande_rdv ORDER BY created_at DESC"
            : "SELECT id, description, created_at, patient_id, specialite, statut FROM demande_rdv " + whereClause + " ORDER BY created_at DESC";

        List<DemandeRDV> list = new ArrayList<>();
        try (PreparedStatement ps = db.prepareStatement(sql)) {
            if (patientId != null) ps.setInt(1, patientId);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRowToDemande(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Error selecting demandes", e);
        }
        return list;
    }
    

    private DemandeRDV mapRowToDemande(ResultSet rs) throws SQLException {
        DemandeRDV d = ObjectFactory.createDemandeRDV();
        d.setId(rs.getInt("id"));
        d.setDescription(rs.getString("description"));
        Timestamp created = rs.getTimestamp("created_at");
        d.setCreatedAt(created != null ? created.toLocalDateTime() : null);

        if (d.getPatient() == null) {
            d.setPatient(ObjectFactory.createPatient());
        }

        d.getPatient().setId(rs.getInt("patient_id"));
        String spec = rs.getString("specialite");
        d.setSpecialite(spec != null ? Specialite.valueOf(spec) : null);
        String st = rs.getString("statut");
        d.setStatut(st != null ? Statut.valueOf(st) : null);
        return d;
    }
}
