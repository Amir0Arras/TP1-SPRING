package pharmacie.dao;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import pharmacie.entity.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class RepositoryCustomMethodsTest {

    @Autowired
    private CategorieRepository categorieRepository;
    @Autowired
    private MedicamentRepository medicamentRepository;
    @Autowired
    private CommandeRepository commandeRepository;
    @Autowired
    private DispensaireRepository dispensaireRepository;
    @Autowired
    private LigneRepository ligneRepository;


    @Test // Ce test se base uniquement sur les données définies dans data.sql
    public void testMedicamentCustomMethods() {    
        Medicament indisponible = medicamentRepository.findByNom("Lévofloxacine 500mg").orElseThrow();
        Medicament disponible   = medicamentRepository.findByNom("Doliprane Effervescent 1g").orElseThrow();
    
        // Trouve tous les médicaments disponibles
        List<Medicament> disponibles = medicamentRepository.findByIndisponibleFalse();

        assertTrue(disponibles.contains(disponible));
        assertFalse(disponibles.contains(indisponible));        
        assertFalse(disponibles.isEmpty());
    }

    @Test // Ce test crée les enregistrements nécessaires
    public void testCategorieCustomMethods() {
        Categorie c1 = new Categorie();
        c1.setLibelle("AnalgesiquesTest");
        categorieRepository.save(c1);

        Categorie c2 = new Categorie();
        c2.setLibelle("AntibiotiquesTest");
        categorieRepository.save(c2);

        // findByLibelle
        Categorie found = categorieRepository.findByLibelle("AnalgesiquesTest");
        assertNotNull(found);
        assertEquals("AnalgesiquesTest", found.getLibelle());

        // findByLibelleContaining
        List<Categorie> list = categorieRepository.findByLibelleContaining("iquesTest");
        assertEquals(2, list.size());
        assertTrue(list.stream().anyMatch(cat -> cat.getLibelle().equals("AntibiotiquesTest")));
        assertTrue(list.stream().anyMatch(cat -> cat.getLibelle().equals("AnalgesiquesTest")));
    }

    @Test
    public void testDispensaireCustomMethods() {
        // findByNom
        Optional<Dispensaire> dispensaire = dispensaireRepository.findByNom("Dispensaire Central");
        assertTrue(dispensaire.isPresent());
        assertEquals("Dispensaire Central", dispensaire.get().getNom());

        // findByVille
        List<Dispensaire> parisiensDispensaires = dispensaireRepository.findByVille("Paris");
        assertEquals(1, parisiensDispensaires.size());
        assertEquals("Paris", parisiensDispensaires.get(0).getVille());

        // findByPays
        List<Dispensaire> francaisDispensaires = dispensaireRepository.findByPays("France");
        assertEquals(3, francaisDispensaires.size());

        // findByRegion
        List<Dispensaire> ileDeFranceDispensaires = dispensaireRepository.findByRegion("Île-de-France");
        assertEquals(1, ileDeFranceDispensaires.size());
        assertEquals("Île-de-France", ileDeFranceDispensaires.get(0).getRegion());
    }

    @Test
    public void testCommandeCustomMethods() {
        // Récupérer un dispensaire
        Dispensaire dispensaire = dispensaireRepository.findByNom("Dispensaire Central").orElseThrow();

        // findByDispensaire
        List<Commande> commandesDispensaire = commandeRepository.findByDispensaire(dispensaire);
        assertFalse(commandesDispensaire.isEmpty());
        assertTrue(commandesDispensaire.stream().allMatch(c -> c.getDispensaire().equals(dispensaire)));

        // findByEnvoyeeLeAfter
        LocalDate dateAfter = LocalDate.of(2026, 1, 9);
        List<Commande> commandesAfter = commandeRepository.findByEnvoyeeLeAfter(dateAfter);
        assertFalse(commandesAfter.isEmpty());
        assertTrue(commandesAfter.stream().allMatch(c -> c.getEnvoyeeLe().isAfter(dateAfter)));

        // findBySaisieLeBefore
        LocalDate dateBefore = LocalDate.of(2026, 1, 11);
        List<Commande> commandesBefore = commandeRepository.findBySaisieLeBefore(dateBefore);
        assertFalse(commandesBefore.isEmpty());
        assertTrue(commandesBefore.stream().allMatch(c -> c.getSaisieLe().isBefore(dateBefore)));

        // findBySaisieLeAfter
        LocalDate dateSaisieAfter = LocalDate.of(2026, 1, 7);
        List<Commande> commandesSaisieAfter = commandeRepository.findBySaisieLeAfter(dateSaisieAfter);
        assertTrue(commandesSaisieAfter.size() >= 3);
    }

    @Test 
    public void testLigneCustomMethods() {
        // Récupérer une commande et un médicament
        Commande commande = commandeRepository.findAll().get(0);
        Medicament medicament = medicamentRepository.findAll().get(0);

        // findByCommande
        List<Ligne> lignesCommande = ligneRepository.findByCommande(commande);
        assertFalse(lignesCommande.isEmpty());
        assertTrue(lignesCommande.stream().allMatch(l -> l.getCommande().equals(commande)));

        // findByMedicament
        List<Ligne> lignesMedicament = ligneRepository.findByMedicament(medicament);
        assertTrue(lignesMedicament.stream().allMatch(l -> l.getMedicament().equals(medicament)));
    }

}
