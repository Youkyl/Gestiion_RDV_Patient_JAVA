package com.grdvp.repository;

import com.grdvp.config.DatabaseConnection;
import com.grdvp.entity.DemandeRDV;
import com.grdvp.entity.Patient;
import com.grdvp.entity.Specialite;
import com.grdvp.entity.Statut;
import com.grdvp.factory.ObjectFactory;
import com.grdvp.repository.interfaces.DemandeRDVRepositoryImpl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;

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

        EntityManager entityManager = ObjectFactory.getEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();

        try {
            transaction.begin();

            if (demande.getPatient() == null || demande.getPatient().getId() == null) {
                throw new IllegalArgumentException("Patient must be attached to the demande with a valid id");
            }

            demande.setStatut(demande.getStatut() != null ? demande.getStatut() : Statut.EN_COURS);
            demande.setPatient(entityManager.getReference(Patient.class, demande.getPatient().getId()));

            entityManager.persist(demande);
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
            throw new RuntimeException("Error inserting demande", e);
        }
    }

    
    public List<DemandeRDV> selectDemande(int patientId) {
        return findDemandesByCondition("WHERE d.patient.id = :patientId", patientId);
    }

    
    public List<DemandeRDV> selectDemandeByStatut(String statut, int patientId) {

        EntityManager entityManager = ObjectFactory.getEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();

        String sql = (patientId == 0)
            ? "SELECT d FROM DemandeRDV d WHERE d.statut = :statut ORDER BY createdAt DESC"
            : "SELECT d FROM DemandeRDV d WHERE d.statut = :statut AND d.patient.id = :patientId ORDER BY createdAt DESC";

        TypedQuery<DemandeRDV> list;

        try {
            transaction.begin();

            list = entityManager.createQuery(sql, DemandeRDV.class);
            list.setParameter("statut", Statut.valueOf(statut));
            

            if (patientId != 0) {
                list.setParameter("patientId", patientId);
            }            

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
            throw new RuntimeException("Error selecting demandes", e);
        }
        return list.getResultList();
    }

    
    public List<DemandeRDV> selectAppointment(int patientId) {
        return selectDemandeByStatut(Statut.ACCEPTE.name(), patientId);
    }


    public void updateStatut(int demandeId, Statut statut) {
        Objects.requireNonNull(statut, "Statut cannot be null");

        EntityManager entityManager = ObjectFactory.getEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();

        String sql = "UPDATE DemandeRDV d SET d.statut = :statut WHERE d.id = :id";

        try {
            transaction.begin();

            entityManager.createQuery(sql).setParameter("statut", statut)
                .setParameter("id", demandeId)
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
            throw new RuntimeException("Error updating demande statut", e);
        }
    }
   
    
    public DemandeRDV findById(int demandeId) {

        EntityManager entityManager = ObjectFactory.getEntityManager();
        try {
            return entityManager.find(DemandeRDV.class, demandeId);
        } catch (Exception e) {
            throw new RuntimeException("Error finding demande by ID", e);
        }
    }


    public List<DemandeRDV> findAll() {
        return findDemandesByCondition("", null);
    }


    private List<DemandeRDV> findDemandesByCondition(String whereClause, Integer patientId) {

        EntityManager entityManager = ObjectFactory.getEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();

        String sql = (whereClause == null || whereClause.isEmpty())
            ? "SELECT d FROM DemandeRDV d ORDER BY d.createdAt DESC"
            : "SELECT d FROM DemandeRDV d " + whereClause + " ORDER BY d.createdAt DESC";

        List<DemandeRDV> list = new ArrayList<>();

        try {
            transaction.begin();

            list = entityManager.createQuery(sql, DemandeRDV.class).setParameter("patientId", patientId).getResultList();

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
            throw new RuntimeException("Error selecting demandes", e);
        }
        return list;
    }
    
}
