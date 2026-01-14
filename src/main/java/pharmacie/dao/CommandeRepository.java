package pharmacie.dao;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

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
}