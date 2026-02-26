package pharmacie.config;

import java.util.Arrays;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import pharmacie.dao.CategorieRepository;
import pharmacie.dao.FournisseurRepository;
import pharmacie.entity.Categorie;
import pharmacie.entity.Fournisseur;

@Configuration
public class LoadDatabase {

    @Bean
    CommandLineRunner initData(CategorieRepository categorieRepository, FournisseurRepository fournisseurRepository) {
        return args -> {
            // 1. Création de 4 fournisseurs
            Fournisseur f1 = new Fournisseur("PharmaLog", "faugeresbaptiste@gmail.com");
            Fournisseur f2 = new Fournisseur("MediDistri", "faugeresbaptiste@gmail.com");
            Fournisseur f3 = new Fournisseur("SanteGlobal", "faugeresbaptiste@gmail.com");
            Fournisseur f4 = new Fournisseur("BioFournisseur", "faugeresbaptiste@gmail.com");

            fournisseurRepository.saveAll(Arrays.asList(f1, f2, f3, f4));

            // 2. Récupération des catégories (on suppose qu'elles existent déjà ou on en
            // crée)
            List<Categorie> categories = categorieRepository.findAll();

            // Si la base est vide, on en crée pour tester
            if (categories.isEmpty()) {
                categories = Arrays.asList(
                        new Categorie("Antibiotiques"),
                        new Categorie("Antalgiques"),
                        new Categorie("Cardiologie"));
                categorieRepository.saveAll(categories);
            }

            // 3. Algorithme pour assigner au moins 2 fournisseurs différents par catégorie
            // On utilise un index rotatif pour varier les fournisseurs
            int fournisseurIndex = 0;
            List<Fournisseur> allFournisseurs = fournisseurRepository.findAll();

            for (Categorie cat : categories) {
                // On prend le fournisseur actuel
                Fournisseur fournisseurA = allFournisseurs.get(fournisseurIndex % allFournisseurs.size());
                // Et le suivant dans la liste
                Fournisseur fournisseurB = allFournisseurs.get((fournisseurIndex + 1) % allFournisseurs.size());

                cat.addFournisseur(fournisseurA);
                cat.addFournisseur(fournisseurB);

                // On sauvegarde la mise à jour
                categorieRepository.save(cat);

                // On incrémente pour que la prochaine catégorie ait des fournisseurs différents
                fournisseurIndex++;
            }

            System.out.println("--- DONNÉES INITIALISÉES : Fournisseurs liés aux Catégories ---");
        };
    }
}