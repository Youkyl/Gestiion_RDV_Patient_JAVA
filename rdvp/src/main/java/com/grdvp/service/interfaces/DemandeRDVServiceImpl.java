package com.grdvp.service.interfaces;

import com.grdvp.entity.DemandeRDV;
import com.grdvp.entity.Patient;
import com.grdvp.entity.Specialite;
import com.grdvp.entity.Statut;
import java.util.List;

public interface DemandeRDVServiceImpl {

    DemandeRDV createDemandeRDV(Patient patient, Specialite specialite, String description);
    
    void addDemand(DemandeRDV demande);

    List<DemandeRDV> searchDemand(Patient patient);

    List<DemandeRDV> filterDemandByStatus(String statut, int patientId);

    List<DemandeRDV> searchApointment(int patientId);

    DemandeRDV serchDemandeById(int demandeId);

    void changeDemandeStatut(DemandeRDV demande, Statut statut);
    
    List<DemandeRDV> getAllDemande();
}
