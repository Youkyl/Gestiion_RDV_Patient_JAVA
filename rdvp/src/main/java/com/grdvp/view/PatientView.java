package com.grdvp.view;

import com.grdvp.entity.DemandeRDV;
import com.grdvp.entity.Patient;
import com.grdvp.entity.Specialite;
import com.grdvp.entity.Statut;
import com.grdvp.service.DemandeRDVService;
import com.grdvp.service.PatientService;

import java.time.LocalDate;
import java.util.Scanner;

public class PatientView {

    private PatientService patientServ;
    private Scanner scanner;
    private DemandeRDVService demandeServ;

    public PatientView()
    {
        this.scanner = new Scanner(System.in);
        this.demandeServ = DemandeRDVService.getInstance();
        this.patientServ = PatientService.getInstance();
    }
    
    private void menuConnexion()
    {
        System.out.println("Bienvenue dans le gestionnaire de Rendez-vous Patient");
        System.out.println("1- Creer un compte Patient");
        System.out.println("2- Se connecter");
        System.out.println("3- Quitter");
        System.out.println("Veillez faire un choix");
    }

    private void menuPatient(Patient patient)
    {
        System.out.println("Bienvenue " + patient.getLastname() + " " + patient.getFirstname() + " dans votre espace Patient");
        System.out.println("1- Ajouter des informations personnelles");
        System.out.println("2- Voir vos informations personnelles");
        System.out.println("3- Ajouter des antecedents medicaux");
        System.out.println("4- Faire une demande de rendez-vous");
        System.out.println("5- Consulter vos demandes de rendez-vous");
        System.out.println("6- Consulter vos rendez-vous");
        System.out.println("7- Changer le statut d'une demande de rendez-vous");
        System.out.println("8- Se deconnecter");   
        System.out.println("Veillez faire un choix");  
    }

    public void mainMenu()
    {
        int choix1;
        int choix2;
        do {
            menuConnexion();
            choix1 = scanner.nextInt();
            scanner.nextLine(); 

            switch (choix1) {
                case 1:
                    System.out.println("Creation de compte Patient");
                    System.out.println("Veuillez entrer votre nom: ");
                    String nom = scanner.nextLine();

                    System.out.println("Veuillez entrer votre prenom: ");
                    String prenom = scanner.nextLine();

                    System.out.println("Veuillez entrer votre email: ");
                    String email = scanner.nextLine();

                    System.out.println("Veuillez entrer votre mot de passe: ");
                    String password = scanner.nextLine();  
                    
                    // System.out.println("Veillez entrer votre addresse de domicile: ");
                    // String address = scanner.nextLine();

                    System.out.println("Veillez entrer votre numero de telephone: ");
                    String number = scanner.nextLine();
                    
                    // System.out.println("Veillez entrer votre date de naissance: ");
                    // LocalDate birthday = LocalDate.parse(scanner.nextLine());
                    
                    // Patient patient = ObjectFactory.createPatient(
                    //     nom, 
                    //     prenom, 
                    //     number, 
                    //     email, 
                    //     password
                    // );

                    Patient patient = patientServ.createPatientCode(nom, prenom, number, email, password);

                    //System.out.println("Patient ID avant insertion demande : " + patient.getId());

                    patientServ.addPatient(patient);
                    break;

                case 2:
                    System.out.println("Connexion Patient");
                    System.out.println("Veuillez entrer votre email: ");
                    String emailC = scanner.nextLine();

                    System.out.println("Veuillez entrer votre mot de passe: ");
                    String passwordC = scanner.nextLine();

                    Patient patientC = patientServ.connexion(emailC, passwordC);
                    if (patientC != null) {

                        do {
                            menuPatient(patientC);
                            choix2 = scanner.nextInt();
                            scanner.nextLine();

                            switch (choix2) {
                                case 1:
                                    System.out.println("Patient ID : " + patientC.getId());

                                    System.out.println("Ajouter des informations personnelles pour le patient: " + patientC.getLastname() + " " + patientC.getFirstname()); 

                                    System.out.println("Veillez entrer votre date de naissance (format: YYYY-MM-DD): ");
                                    LocalDate birthday = LocalDate.parse(scanner.nextLine());

                                    System.out.println("Veillez entrer votre addresse de domicile: ");
                                    String address = scanner.nextLine(); 

                                    patientServ.completePatientInfo(patientC, birthday, address);

                                    break;

                                case 2:
                                    System.out.println("Mes information personnelles:");
                                    //System.out.println("Patient ID : " + patientC.getId());
                                    
                                    var info = patientServ.getConnectedPatientInfo(patientC.getId());

                                    System.out.println(info.toString());

                                    break;
                                case 3:
                                    System.out.println("Ajouter des antecedents medicaux ");

                                    System.out.println("Veillez saisir des antecedents medicaux: ");
                                    // List<String> medicalHistory = scanner.nextLine().lines().toList();
                                    String medicalHistory = scanner.nextLine();

                                    patientServ.addAntecedent(patientC, medicalHistory);
                                    break;
                                case 4:
                                    System.out.println("Faire une demande de rendez-vous");

                                    System.out.println("Veillez choisir dans la liste une specialite");
                                    for (Specialite s : Specialite.values()) {
                                        System.out.println("- " + s.name());
                                    }
                                    Specialite spe = Specialite.valueOf(scanner.nextLine());

                                    System.out.println("Veillez saisir une description pour votre demande de rendez-vous (Optionnel): ");
                                    String description = scanner.nextLine();

                                    DemandeRDV demande = demandeServ.createDemandeRDV(patientC, spe, description);
                                    demandeServ.addDemand(demande);
                                    break;

                                case 5:
                                    System.out.println("Consulter vos demandes de rendez-vous");

                                    var list = demandeServ.searchDemand(patientC);

                                    for (DemandeRDV demandeRDV : list) {
                                        System.out.println("Patient id: " + demandeRDV.getPatientId() + ", Specialite: " + demandeRDV.getSpecialite() + ", Description: " + demandeRDV.getDescription() + ", Statut de la demande: " + demandeRDV.getStatut());
                                    }

                                    break;

                                case 6:
                                    System.out.println("Consulter vos rendez-vous");

                                    list = demandeServ.searchApointment(patientC.getId());

                                    for (DemandeRDV demandeRDV : list) {
                                        System.out.println("Patient id: " + demandeRDV.getPatientId() + ", Specialite: " + demandeRDV.getSpecialite() + ", Description: " + demandeRDV.getDescription() + ", Statut de la demande: " + demandeRDV.getStatut());
                                    }

                                    break;

                                case 7:
                                    System.out.println("Changer le statut d'une demande de rendez-vous");

                                    System.out.println("Veillez saisir l'ID de la demande de rendez-vous: ");
                                    int demandeId = scanner.nextInt();
                                    scanner.nextLine(); 

                                    System.out.println("Veillez choisir le nouveau statut de votre demande de rendez-vous: ");
                                    for (Statut s : Statut.values()) {
                                        System.out.println("- " + s.name());
                                    }
                                    String newStatut = scanner.nextLine();
                                    
                                    DemandeRDV d = demandeServ.serchDemandeById(demandeId);

                                    demandeServ.changeDemandeStatut(d, Statut.valueOf(newStatut));
                                    
                                    break;
                                default:
                                    break;
                            }
                            
                        } while (choix2 != 8);

                    } else {
                        System.out.println("Email ou mot de passe incorrect.");
                    }
                    break;

                default:
                    System.out.println("Choix invalide. Veuillez réessayer.");
                    break;
            }
        } while (choix1 != 3);
    }
}
