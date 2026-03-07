package com.grdvp.repository.interfaces;

import com.grdvp.entity.DemandeRDV;
import com.grdvp.entity.Statut;
import java.util.List;

public interface DemandeRDVRepositoryImpl {

    void insertDemande(DemandeRDV demande);

    List<DemandeRDV> selectDemande(int patientId);

    List<DemandeRDV> selectDemandeByStatut(String statut, int patientId);

    List<DemandeRDV> selectAppointment(int patientId);

    DemandeRDV findById(int demandeId);

    void updateStatut(int demandeId, Statut statut);

    List<DemandeRDV> findAll();
}
