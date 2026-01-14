package pharmacie.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import pharmacie.entity.Ligne;
import pharmacie.entity.Commande;
import pharmacie.entity.Medicament;

public interface LigneRepository extends JpaRepository<Ligne, Integer> {

    /**
     * Recherche toutes les lignes pour une commande donnée
     */
    List<Ligne> findByCommande(Commande commande);

    /**
     * Recherche toutes les lignes pour un médicament donné
     */
    List<Ligne> findByMedicament(Medicament medicament);
}
