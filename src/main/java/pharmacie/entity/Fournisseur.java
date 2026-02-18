package pharmacie.entity;

import java.util.HashSet; // ou javax.persistence.* si ancienne version de Spring
import java.util.Set;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;

@Entity
public class Fournisseur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nom;
    private String email;

    // Relation inverse : on pointe vers la liste "fournisseurs" dans Categorie
    @ManyToMany(mappedBy = "fournisseurs")
    private Set<Categorie> categories = new HashSet<>();

    public Fournisseur() {
    }

    public Fournisseur(String nom, String email) {
        this.nom = nom;
        this.email = email;
    }

    // Getters et Setters
    public Long getId() { return id; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public Set<Categorie> getCategories() { return categories; }
}