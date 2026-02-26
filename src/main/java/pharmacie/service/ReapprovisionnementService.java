package pharmacie.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import pharmacie.dao.MedicamentRepository;
import pharmacie.entity.Categorie;
import pharmacie.entity.Fournisseur;
import pharmacie.entity.Medicament;

@Service
public class ReapprovisionnementService {

    @Autowired
    private MedicamentRepository medicamentRepository;

    @Autowired
    private JavaMailSender mailSender;

    public void processReapprovisionnement() {
        // 1. Récupérer les médicaments concernés
        List<Medicament> medicamentsAReappro = medicamentRepository.findMedicamentsAReapprovisionner();

        // 2. Regrouper les données : Map<Fournisseur, Map<Categorie, List<Medicament>>>
        Map<Fournisseur, Map<Categorie, List<Medicament>>> commandesParFournisseur = new HashMap<>();

        for (Medicament medicament : medicamentsAReappro) {
            Categorie categorie = medicament.getCategorie();

            // Pour chaque fournisseur de cette catégorie, on ajoute le médicament
            for (Fournisseur fournisseur : categorie.getFournisseurs()) {
                commandesParFournisseur.putIfAbsent(fournisseur, new HashMap<>());
                Map<Categorie, List<Medicament>> medicamentsDuFournisseur = commandesParFournisseur.get(fournisseur);

                medicamentsDuFournisseur.putIfAbsent(categorie, new ArrayList<>());
                medicamentsDuFournisseur.get(categorie).add(medicament);
            }
        }

        // 3. Envoyer un mail unique par fournisseur
        for (Map.Entry<Fournisseur, Map<Categorie, List<Medicament>>> entry : commandesParFournisseur.entrySet()) {
            Fournisseur fournisseur = entry.getKey();
            Map<Categorie, List<Medicament>> medicamentsParCategorie = entry.getValue();

            envoyerMailDevis(fournisseur, medicamentsParCategorie);
        }
    }

    private void envoyerMailDevis(Fournisseur fournisseur, Map<Categorie, List<Medicament>> medicamentsParCategorie) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(fournisseur.getEmail());
        message.setSubject("Demande de devis - Réapprovisionnement Pharmacie");

        StringBuilder corpsDuMail = new StringBuilder();
        corpsDuMail.append("Bonjour ").append(fournisseur.getNom()).append(",\n\n");
        corpsDuMail.append("Veuillez nous transmettre un devis pour réapprovisionner les médicaments suivants :\n\n");

        for (Map.Entry<Categorie, List<Medicament>> catEntry : medicamentsParCategorie.entrySet()) {
            Categorie categorie = catEntry.getKey();
            // Adaptez le getNom() si la propriété s'appelle autrement (ex: getLibelle())
            corpsDuMail.append("--- Catégorie : ").append(categorie.getLibelle()).append(" ---\n");

            for (Medicament medicament : catEntry.getValue()) {
                corpsDuMail.append(" • ").append(medicament.getNom())
                        .append(" (Stock actuel : ").append(medicament.getUnitesEnStock())
                        .append(", Seuil de réappro : ").append(medicament.getNiveauDeReappro()).append(")\n");
            }
            corpsDuMail.append("\n");
        }

        corpsDuMail.append("Dans l'attente de votre retour,\nCordialement,\nLa Pharmacie.");

        message.setText(corpsDuMail.toString());

        // Envoi effectif
        mailSender.send(message);
        System.out.println("Mail de devis envoyé à : " + fournisseur.getEmail());
    }
}