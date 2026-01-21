package pharmacie.dao;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import pharmacie.entity.Commande;
import pharmacie.entity.Dispensaire;

public interface CommandeRepository extends JpaRepository<Commande, Integer> {

    /**
     * Recherche toutes les commandes d'un dispensaire
     */
    List<Commande> findByDispensaire(Dispensaire dispensaire);

    /**
     * Recherche les commandes envoyées après une date donnée
     */
    List<Commande> findByEnvoyeeLeAfter(LocalDate date);

    /**
     * Recherche les commandes saisies avant une date donnée
     */
    List<Commande> findBySaisieLeBefore(LocalDate date);
     List<Commande> findBySaisieLeAfter(LocalDate date);

    /**
     * Requête 1: Calculer le nombre d'articles (somme des quantités) 
     * déjà commandés par un dispensaire (envoyeeLe doit être renseigné)
     */
    @Query("SELECT COALESCE(SUM(l.quantite), 0) FROM Ligne l " +
           "JOIN l.commande c " +
           "WHERE c.dispensaire.code = :dispensaireCode AND c.envoyeeLe IS NOT NULL")
    Long countArticlesOrderedByDispensaire(@Param("dispensaireCode") Integer dispensaireCode);

    /**
     * Requête 2: Trouver toutes les commandes en cours pour un dispensaire
     * Une commande est en cours si envoyeeLe est NULL (non renseignée)
     */
    @Query("SELECT c FROM Commande c " +
           "WHERE c.dispensaire.code = :dispensaireCode AND c.envoyeeLe IS NULL " +
           "ORDER BY c.saisieLe DESC")
    List<Commande> findOngoingCommandesByDispensaire(@Param("dispensaireCode") Integer dispensaireCode);
}