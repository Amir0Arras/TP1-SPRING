package pharmacie.dao;

import java.util.Optional;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import pharmacie.entity.Dispensaire;

public interface DispensaireRepository extends JpaRepository<Dispensaire, Integer> {

    /**
     * Recherche un dispensaire par son nom (unique)
     */
    Optional<Dispensaire> findByNom(String nom);

    /**
     * Recherche les dispensaires situés dans une ville donnée
     */
    List<Dispensaire> findByVille(String ville);

    /**
     * Recherche les dispensaires d'un pays donné
     */
    List<Dispensaire> findByPays(String pays);
    List<Dispensaire> findByRegion(String region);
}
