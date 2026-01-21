package pharmacie.dao;


import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import pharmacie.entity.Medicament;

// Cette interface sera auto-implémentée par Spring
public interface MedicamentRepository extends JpaRepository<Medicament, Integer> {
    /**
     * Trouve un médicament à partir de son nom (unique dans Medicament)
     * @return un médicament "optionnel"
     */
    Optional<Medicament>findByNom(String nom);

    /**
     * Trouve les médicaments disponibles (indisponible = false)
     * @return la liste des médicaments disponibles
     */
    List<Medicament> findByIndisponibleFalse();
     
    /**
     * Requête 3: Trouver tous les médicaments disponibles à la commande pour une catégorie
     * Un médicament est disponible à la commande si:
     * - Il n'est pas indisponible (indisponible = false)
     * - Sa quantité en stock >= quantité en commande (unitesEnStock >= unitesCommandees)
     */
    @Query("SELECT m FROM Medicament m " +
           "WHERE m.categorie.code = :categorieCode " +
           "AND m.indisponible = false " +
           "AND m.unitesEnStock >= m.unitesCommandees " +
           "ORDER BY m.nom ASC")
    List<Medicament> findAvailableMedicamentsByCategorie(@Param("categorieCode") Integer categorieCode);
}
